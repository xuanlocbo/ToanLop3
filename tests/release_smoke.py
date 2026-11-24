"""Verify the non-debug release launches and retains data after reinstall with the same key."""
import subprocess, time
from pathlib import Path
from native_smoke import adb, tap, expect, nodes, launch, screenshot, pause
PKG="vn.toanlop3"
def main():
    adb("uninstall",PKG,check=False)
    adb("install","dist/build/qa-release.apk")
    adb("shell","wm","size","720x1280");adb("shell","wm","density","320")
    adb("logcat","-c");launch();tap(desc="Học và chơi bài 25")
    expect("35 : 7");tap(desc="Chữ số 5");tap(desc="Ô số dư");tap(desc="Chữ số 0")
    adb("shell","am","force-stop",PKG);adb("install","-r","dist/build/qa-release.apk");launch()
    expect("35 : 7")
    ui=nodes()
    for desc,value in [("Ô thương","5"),("Ô số dư","0")]:
        assert any(n.get("content-desc")==desc and n.get("text")==value for n in ui),(desc,value)
    tap(desc="Kiểm tra đáp án");expect("Chính xác!");screenshot("09_release_reinstall")
    assert "FATAL EXCEPTION" not in adb("logcat","-d","-s","AndroidRuntime:E")
    print("RELEASE_SMOKE_PASS non-debug installation, input saved, same-signature reinstall.")
if __name__=="__main__":main()
