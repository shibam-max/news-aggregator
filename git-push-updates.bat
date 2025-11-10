@echo off
echo ========================================
echo GITHUB PUSH COMMANDS - NEWS AGGREGATOR
echo ========================================

echo.
echo STEP 1: Configure Git (if not already done)
echo ----------------------------------------
echo git config --global user.name "shibam-max"
echo git config --global user.email "shibamsamaddar1999@gmail.com"
echo.

echo STEP 2: Initialize Git Repository (if not already done)
echo ----------------------------------------
echo git init
echo.

echo STEP 3: Add Remote Repository (replace with your repo URL)
echo ----------------------------------------
echo git remote add origin https://github.com/shibam-max/news-aggregator-enterprise.git
echo OR if repo exists:
echo git remote set-url origin https://github.com/shibam-max/news-aggregator-enterprise.git
echo.

echo STEP 4: Add All Files
echo ----------------------------------------
echo git add .
echo.

echo STEP 5: Commit Changes
echo ----------------------------------------
echo git commit -m "feat: Complete Enterprise News Aggregator with all features"
echo.

echo STEP 6: Push to GitHub
echo ----------------------------------------
echo git branch -M main
echo git push -u origin main
echo.

echo ========================================
echo EXECUTE THESE COMMANDS ONE BY ONE:
echo ========================================
echo.
echo 1. git config --global user.name "shibam-max"
echo 2. git config --global user.email "shibamsamaddar1999@gmail.com"
echo 3. git init
echo 4. git remote add origin https://github.com/shibam-max/YOUR_REPO_NAME.git
echo 5. git add .
echo 6. git commit -m "feat: Complete Enterprise News Aggregator with all features"
echo 7. git branch -M main
echo 8. git push -u origin main
echo.
echo ========================================