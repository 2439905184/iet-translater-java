@echo off
set JLINK_VM_OPTIONS=
set BIN_DIR=%~dp0
set PATH=%BIN_DIR%

set DEBUG_MODE=false
start /MIN "" "%BIN_DIR%\javaw" %JLINK_VM_OPTIONS% -m _128hh_trans_jp2_zh/com.bigbrain._128hh_trans_jp2_zh.Launcher %*