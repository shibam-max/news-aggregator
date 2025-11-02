# API Keys Setup Guide

This guide shows you exactly how to get your News Aggregator running with real Guardian and NY Times APIs.

## Step 1: Get Your API Keys

### Guardian API Key (Free)
1. Visit: https://open-platform.theguardian.com/access/
2. Click "Register for a developer key"
3. Fill out the form (use any website URL like http://localhost:8080)
4. Copy your API key when approved (usually instant)

### NY Times API Key (Free)
1. Visit: https://developer.nytimes.com/get-started
2. Create a free account
3. Go to "My Apps" and create a new app
4. Subscribe to "Article Search API" (free tier: 1000 requests/day)
5. Copy your API key from the app details

## Step 2: Configure Your Application (Choose ONE method)

### Option A: IntelliJ IDEA (Easiest for Development)

**Method 1: Through Run Configuration**
1. In IntelliJ, go to **Run → Edit Configurations**
2. Select your `NewsAggregatorApplication` configuration (or create new if none exists)
3. Look for **Environment Variables** section:
   - **IntelliJ 2022+**: Click the folder icon 📁 next to "Environment variables" 
   - **IntelliJ 2020-2021**: Click "..." button next to "Environment variables" field
   - **Older versions**: Look for "Environment variables" text field
4. In the popup window, click **+** (plus) button and add:
   ```
   Name: GUARDIAN_API_KEY
   Value: paste-your-guardian-key-here
   
   Name: NYTIMES_API_KEY  
   Value: paste-your-nytimes-key-here
   ```
5. Click **OK** twice and run the application

**Method 2: If you can't find Environment Variables**
1. Go to **Run → Edit Configurations**
2. In the left panel, right-click and select **+ → Application**
3. Set:
   - **Name**: NewsAggregator
   - **Main class**: `com.newsaggregator.NewsAggregatorApplication`
   - **Working directory**: Your project root directory
4. Now look for **Environment Variables** and follow Method 1 steps 3-5

**Method 3: Alternative - Using VM Options (If Environment Variables not available)**
1. In **Run → Edit Configurations**
2. Find **VM Options** field (usually easier to find than Environment Variables)
3. Add these VM options:
   ```
   -DGUARDIAN_API_KEY=paste-your-guardian-key-here -DNYTIMES_API_KEY=paste-your-nytimes-key-here
   ```
4. Click **OK** and run

**Method 4: Quick Test - Use Command Line Instead**
If IntelliJ is giving trouble, just use the command line:
```bash
# Open terminal in IntelliJ (Alt+F12) or external terminal
set GUARDIAN_API_KEY=your-guardian-key-here
set NYTIMES_API_KEY=your-nytimes-key-here
mvnw spring-boot:run
```

### Option B: Windows Command Line
```bash
set GUARDIAN_API_KEY=paste-your-guardian-key-here
set NYTIMES_API_KEY=paste-your-nytimes-key-here
mvnw spring-boot:run
```

### Option C: PowerShell
```powershell
$env:GUARDIAN_API_KEY="paste-your-guardian-key-here"
$env:NYTIMES_API_KEY="paste-your-nytimes-key-here"
.\mvnw spring-boot:run
```

### Option D: Docker (Production Ready)
```bash
docker run -p 8080:8080 \
  -e GUARDIAN_API_KEY=paste-your-guardian-key-here \
  -e NYTIMES_API_KEY=paste-your-nytimes-key-here \
  news-aggregator:latest
```

### Option E: Docker Compose
Create a `.env` file in your project root:
```env
GUARDIAN_API_KEY=paste-your-guardian-key-here
NYTIMES_API_KEY=paste-your-nytimes-key-here
```

Then run:
```bash
docker-compose up
```

## Step 3: Test Real API Integration

1. **Start the application** using one of the methods above
2. **Test with real news data**:
   ```
   http://localhost:8080/api/v1/news/search?keyword=technology&offlineMode=false
   ```
3. **Expected result**: Real news articles from Guardian and NY Times!

## Step 4: Verify It's Working

### Success Indicators:
- ✅ Articles have real news titles
- ✅ URLs point to guardian.com and nytimes.com  
- ✅ Recent publication dates
- ✅ Response shows `"fromCache": false, "offlineMode": false`

### If It's Not Working:
- ❌ Empty articles array → Check your API keys
- ❌ 401/403 errors → Verify key permissions  
- ❌ Still getting offline data → Ensure `offlineMode=false`

## API Rate Limits

**Guardian**: 5000 requests/day (free tier)  
**NY Times**: 1000 requests/day (free tier)

For production, consider upgrading to paid tiers for higher limits.

## Security Note

**Never commit real API keys to version control!**
- Use environment variables (as shown above)
- Add `.env` to your `.gitignore` file
- Use different keys for different environments

## Troubleshooting

### Common Issues:

1. **"Invalid API Key" Error**
   - Double-check you copied the key correctly
   - Ensure no extra spaces or characters

2. **"API Key Not Found" Error**  
   - Verify the environment variables are set correctly
   - Restart your application after setting variables

3. **"Rate Limit Exceeded" Error**
   - Wait 24 hours for limits to reset
   - Consider upgrading to paid API plans

### Test Offline Mode:
If APIs are temporarily down, test offline mode:
```
http://localhost:8080/api/v1/news/search?keyword=business&offlineMode=true
```

Your News Aggregator will automatically fall back to offline mode if APIs fail, ensuring 100% uptime!
