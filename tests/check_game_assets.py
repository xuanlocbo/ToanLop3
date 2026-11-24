"""Verify the exact game resources, before and after Android packaging."""
from pathlib import Path
import sys,zipfile,hashlib
root=Path("app/src/main/res")
expected={
"map_skin.webp":(374376,"3d6871af2ee241fc6aa6eb17356cac43a643133c8a24a4f227338c0c35954051"),
"mascot.webp":(36068,"9469de11a02841fa63332bc6ae55437afe4c451d6fd70c8260e04f9b92287b42"),
"treasure.webp":(13132,"307bcbb329b212a6cf3fc4fd55e84af009d8b21d6552d2a1655bc440e24d5a66"),
"world_0.webp":(166626,"e80d3632a97a2ca4d32c11269f1f8d7fd462e56cef853638f43a5f46bb36ca09"),
"world_1.webp":(116930,"31ea7466b0790277feb7cf0e2c38035e5b382442daaa5ed5f1633755e278c38e"),
"world_2.webp":(69668,"dfb21d83beb0a1b720123493bbc9615c48299aa15312d8a4cbb0fa608513778c"),
"world_3.webp":(109190,"d673406389d79dd75ac5f77ded42ff0d53f6c743171c782b9452e3bd8b8d83af"),
"correct.ogg":(4765,"e8d5903c6eb875c4e9a3ae7e994ff628aa436f03f36b340820d8318f6b137107"),
"jump.ogg":(4194,"f5f49a65b487396102edd623495cd809723e10aa021484e717a2302c72d2ccd6"),
"music_0.ogg":(37988,"5be813c0734b371e9eb2a8bd38b1a082eb8f3548f1f8fa535680aa951eaa41c8"),
"music_1.ogg":(38118,"e8de0532d4eee4b265b4f2b49e1778373c00f6852e6f203b1708451755376aef"),
"music_2.ogg":(38162,"80690e500b2bec9dccb7e76314c71958a8e663037ba4c7681a693a9da39ce793"),
"music_3.ogg":(39206,"77dbb02a85350f43f728ebc2fdc83d9161f11e8bf01ffa8e95c4d0de73300d71"),
"retry.ogg":(4123,"47303a7bc20d8117afa56a606d7bd33d9aeeb7681d54e32bb66e5d3db058be96"),
"tap.ogg":(4000,"dfa05cee84a713e9d889ebc77927fba350fe9a311845145fbdaeb482aca29454"),
"treasure.ogg":(5063,"6ece12c23792256ab888241f0aa3d75da0561d093768d5b8228384b55b7c62a2")}
def check(n,data):
 size,sha=expected[n]
 assert len(data)==size,(n,len(data),size)
 assert hashlib.sha256(data).hexdigest()==sha,n
 if n.endswith(".webp"):assert data[:4]==b"RIFF" and data[8:12]==b"WEBP",n
 else:assert data[:4]==b"OggS",n
for n in expected:check(n,(root/("drawable-nodpi" if n.endswith(".webp") else "raw")/n).read_bytes())
if len(sys.argv)>1:
 with zipfile.ZipFile(sys.argv[1]) as z:
  names=z.namelist()
  for n in expected:
   matches=[k for k in names if k.rsplit("/",1)[-1]==n]
   assert len(matches)==1,("Missing packaged resource",n,matches)
   check(n,z.read(matches[0]))
  assert "AndroidManifest.xml" in names and any(n.endswith(".dex") for n in names)
  assert not any(n.endswith(".html") for n in names)
print("PASS: exact 7 artwork files and 9 original audio clips"+(" in APK" if len(sys.argv)>1 else " in source"))
