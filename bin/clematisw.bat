@echo off
@rem ======================================
@rem DOS Batch file to invoke the frontend
@rem ======================================

@rem Release call
@cd ..
@start javaw -classpath .;launcher.jar; jworkspace.kernel.WorkspaceLauncher -qlogin root
@cd ./bin
@echo on