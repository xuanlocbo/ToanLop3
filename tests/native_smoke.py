#!/usr/bin/env python3
"""Exercise the actual native UI on a running Android emulator, not a source-only assertion."""
import os, re, subprocess, time, xml.etree.ElementTree as ET, json
from pathlib import Path
APK="app/build/outputs/apk/debug/app-debug.apk"
PKG="vn.toanlop3"
COMP=PKG+"/.MainActivity"
QA=Path("dist/qa");QA.mkdir(parents=True,exist_ok=True)
def adb(*a, check=True):
    return subprocess.run(["adb",*a],check=check,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True).stdout
def pause(s=.65):
    time.sleep(s)
def tree():
    last=""
    for i in range(6):
        try:
            adb("shell","uiautomator","dump","/sdcard/toan_ui.xml")
            last=adb("shell","cat","/sdcard/toan_ui.xml")
            return ET.fromstring(last)
        except (ET.ParseError,subprocess.CalledProcessError):
            time.sleep(1)
    raise AssertionError("Could not inspect UI: "+last[:500])
def nodes():
    return list(tree().iter("node"))
def tap(desc=None,text=None,prefix=False):
    for tries in range(4):
        for n in nodes():
            v=n.get("content-desc","") if desc is not None else n.get("text","")
            want=desc if desc is not None else text
            if (v.startswith(want) if prefix else v==want):
                b=list(map(int,re.findall(r"-?\d+",n.get("bounds",""))))
                if len(b)==4 and b[2]>b[0] and b[3]>b[1]:
                    adb("shell","input","tap",str((b[0]+b[2])//2),str((b[1]+b[3])//2));pause();return
        time.sleep(.5)
    raise AssertionError("UI control not visible: "+str(desc or text))
def expect(fragment):
    values=[(n.get("text",""),n.get("content-desc","")) for n in nodes()]
    assert any(fragment in t or fragment in d for t,d in values),("Missing",fragment,values)
def screenshot(name):
    with (QA/(name+".png")).open("wb") as f:subprocess.run(["adb","exec-out","screencap","-p"],stdout=f,check=True)
    (QA/(name+".xml")).write_text(adb("shell","cat","/sdcard/toan_ui.xml"),encoding="utf8")
def launch():
    out=adb("shell","am","start","-W","-n",COMP)
    assert "Error" not in out,out;pause(1.2)
def checkpoint(n):
    raw=adb("shell","run-as",PKG,"cat","shared_prefs/toanlop3.journey.v3.xml")
    root=ET.fromstring(raw)
    return json.loads(next(x.text for x in root if x.get("name")=="core"+str(n)))
def assert_draft(n,quot,rem,cursor):
    st=checkpoint(n)
    assert st["draft"]==[quot,rem],st
    assert st["cursor"]==cursor,st
    return st
def verify_home():
    expect("Toán lớp 3");expect("Mộc Nhiên")
    for i in range(25,45):
        vals=[n.get("content-desc","") for n in nodes()] if i==25 else vals
        assert any(v.startswith("Bài "+str(i)+",") for v in vals),("Missing map node",i,vals)
def main():
    adb("wait-for-device");adb("shell","wm","size","1080x1920");adb("shell","wm","density","360")
    adb("install","-r",APK);adb("shell","pm","clear",PKG);adb("logcat","-c")
    launch();verify_home();screenshot("01_home")
    tap(desc="Học và chơi bài 25");expect("35 : 7");screenshot("02_division")
    tap(desc="Chữ số 5");tap(desc="Ô số dư");tap(desc="Chữ số 0")
    assert_draft(25,"5","0",0);adb("shell","am","force-stop",PKG);launch();expect("35 : 7")
    assert_draft(25,"5","0",0);screenshot("03_restored")
    tap(desc="Kiểm tra đáp án");expect("Chính xác!");screenshot("04_correct")
    st=checkpoint(25);assert st["solved"] and len(st["done"])==1,st
    tap(desc="Tiếp tục nhiệm vụ");expect("45 : 8")
    tap(desc="Chữ số 1");tap(desc="Ô số dư");tap(desc="Chữ số 0");tap(desc="Kiểm tra đáp án")
    expect("Thử lại nhé!");st=checkpoint(25);assert st["wrong"]==1 and not st["solved"],st
    tap(desc="Về hành trình");tap(desc="Bài 30,",prefix=True);tap(desc="Học và chơi bài 30");expect("3 cm = ? mm")
    tap(desc="Chữ số 3");tap(desc="Chữ số 0");tap(desc="Kiểm tra đáp án");expect("Chính xác!")
    tap(desc="Tiếp tục nhiệm vụ");expect("20 mm = ? cm")
    # Verify orientation/size change does not lose the question, then revisit an earlier lesson.
    adb("shell","wm","size","720x1280");adb("shell","wm","density","320");pause(2);expect("20 mm = ? cm")
    screenshot("05_small_screen")
    tap(desc="Về hành trình");tap(desc="Bài 25,",prefix=True);tap(desc="Học và chơi bài 25");expect("45 : 8")
    assert_draft(25,"1","0",1);screenshot("06_return_to_lesson")
    # Continue through real UI, then test a manipulative and its saved state.
    tap(desc="Ô thương");tap(desc="Xóa chữ số");tap(desc="Chữ số 5")
    tap(desc="Ô số dư");tap(desc="Xóa chữ số");tap(desc="Chữ số 5")
    tap(desc="Kiểm tra đáp án");expect("Chính xác!");tap(desc="Tiếp tục nhiệm vụ")
    for problem,quot,rem in [("23 : 3","7","2"),("45 : 5","9","0")]:
        expect(problem);tap(desc="Chữ số "+quot);tap(desc="Ô số dư");tap(desc="Chữ số "+rem)
        tap(desc="Kiểm tra đáp án");expect("Chính xác!");tap(desc="Tiếp tục nhiệm vụ")
    expect("Chọn các phép chia hết.")
    for opt in ["30 : 6","42 : 7","27 : 9"]:tap(desc="Chọn "+opt)
    tap(desc="Kiểm tra đáp án");expect("Chính xác!");tap(desc="Tiếp tục nhiệm vụ")
    expect("Chia đều 20 quả táo.")
    lab=next(n for n in nodes() if n.get("content-desc","").startswith("Chia 20 quả táo vào đĩa:"))
    b=list(map(int,re.findall(r"-?\d+",lab.get("bounds",""))))
    l,t,r,bot=b;sc=min((r-l)/850,(bot-t)/390);ox=l+((r-l)-850*sc)/2;oy=t+((bot-t)-390*sc)/2
    adb("shell","input","swipe",str(round(ox+89*sc)),str(round(oy+82*sc)),str(round(ox+244.5*sc)),str(round(oy+270*sc)),"850")
    pause();st=checkpoint(25);assert st["appleLayout"].startswith("1"),st
    adb("shell","am","force-stop",PKG);launch();expect("Chia đều 20 quả táo.")
    assert checkpoint(25)["appleLayout"]==st["appleLayout"]
    screenshot("07_apple_restored")
    for opt in ["2 đĩa","4 đĩa"]:tap(desc="Chọn "+opt)
    tap(desc="Kiểm tra đáp án");expect("Chính xác!");screenshot("08_apple_correct")
    crashes=adb("logcat","-d","-s","AndroidRuntime:E")
    assert "FATAL EXCEPTION" not in crashes,crashes
    print("NATIVE SMOKE PASS: map, real install/start, answer, wrong answer, autosave/relaunch, lesson resume, small-screen resize")
if __name__=="__main__":
    try:main()
    except BaseException:
        try:screenshot("failure")
        except Exception:pass
        print(adb("logcat","-d","-s","AndroidRuntime:E",check=False))
        raise
