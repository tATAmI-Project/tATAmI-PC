@echo Pulling changes from %1.
@echo This assumes you have git installed on your system and in PATH (executing the command 'git' in console writes the possible git operations).
@echo If anything goes wrong, please use the command line to execute commands in this file.
@echo Working in:
@cd
git checkout %2
@echo ====================================
@echo Ready to fetch (ready to insert passphrase)...
@pause
@ssh-agent bash -c "ssh-add $HOME/.ssh/githubkey; git fetch"
@git status
@echo ====================================
@echo Ready to pull (ready to insert passphrase); otherwise close this window
@pause
@ssh-agent bash -c "ssh-add $HOME/.ssh/githubkey; git pull origin %2"
@echo ====================================
@echo Supposedly done.
@pause
@exit