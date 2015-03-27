@echo off
@rem ======================================
@rem DOS Batch file to invoke the frontend
@rem ======================================

@rem Release call
@cd ..
@cd
java -Djava.library.path=./lib -classpath .;./i18n;./lib/_msgconnect_1_21_patch.jar;./lib/_jsdt_patch.jar;./lib/_kiwi_patch.jar;./lib/jw.jar;./lib/sed.jar;./lib/kiwi.jar;./lib/msgconnect.jar;./lib/jimi.jar;./lib/kunststoff.jar;./lib/qflib.jar;./lib/bsh.jar;./lib/xerces.jar;./lib/jdom.jar;./lib/jwnet.jar;./lib/jsdt.jar;./lib/sax.jar;./lib/plastic.jar;./lib/jgraph.jar;./lib/jgrapht.jar;./lib/regexp.jar; jworkspace.kernel.Workspace -qlogin root -loglevel 7
@cd ./bin
@echo on