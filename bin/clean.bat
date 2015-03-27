@echo *****************************************
@echo **                                     **
@echo **    Cleaning build products of       **
@echo **      Clematis 1.0.3 sources         **
@echo **                                     **
@echo *****************************************
@echo **                                     **
@echo **  1. Clematis Launcher               **
@echo **                                     **
@echo *****************************************
@cd "./../src/launcher/"
@call ant clean
@cd "./../../"
@echo *****************************************
@echo **                                     **
@echo **  2. Clematis 1.0.3 core             **
@echo **                                     **
@echo *****************************************
@cd "./bin"
@call ant clean
@cd "./../"
@echo *****************************************
@echo **                                     **
@echo **  3. Kiwi 1.4.3 library patch        **
@echo **                                     **
@echo *****************************************
@cd "./src/kiwi_patch/"
@call ant clean
@cd "./../../"
@echo *****************************************
@echo **                                     **
@echo **  4. Wutka DTD 1.21 library          **
@echo **                                     **
@echo *****************************************
@cd "./src/dtdparser/"
@call ant clean
@cd "./../../"
@echo *****************************************
@echo **                                     **
@echo **  5. Clematis Source Editor 1.0.3    **
@echo **                                     **
@echo *****************************************
@cd "./src/sed/"
@call ant clean
@cd "./../../"
@echo *****************************************
@echo **                                     **
@echo **  6. Runtime UI Shell 0.90           **
@echo **                                     **
@echo *****************************************
@cd "./src/rtui/"
@call ant clean
@cd "./../../"
@echo *****************************************
@echo **                                     **
@echo **  7. Installer UI Shell 0.90         **
@echo **                                     **
@echo *****************************************
@cd "./src/instui/"
@call ant clean
@cd "./../../"
@echo *****************************************
@echo **                                     **
@echo **  8. Clematis Network A              **
@echo **                                     **
@echo *****************************************
@cd "./src/net/"
@call ant clean
@cd "./../../bin"
