# 🚀 IntelliJ IDEA Setup Guide for News Aggregator

## Step 1: Import Project in IntelliJ

1. **Open IntelliJ IDEA**
2. **File → Open** → Navigate to `c:\Java-microservice\news-aggregator`
3. **Select the folder** and click OK
4. **Wait for Maven import** to complete (check bottom status bar)

## Step 2: Configure Project Settings

### Java SDK Configuration
1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
3. **Project SDK**: Select Java 17 (or download if not available)
4. **Project language level**: 17 - Sealed types, always-strict floating-point semantics
5. Click **Apply** and **OK**

### Maven Configuration
1. **File → Settings** (Ctrl+Alt+S)
2. **Build, Execution, Deployment → Build Tools → Maven**
3. **Maven home path**: Use bundled Maven or specify custom path
4. **User settings file**: Default or custom
5. **Local repository**: Default location
6. Click **Apply** and **OK**

## Step 3: Install Required Plugins

1. **File → Settings → Plugins**
2. **Install these plugins:**
   - ✅ **Lombok** (for @Data, @Builder annotations)
   - ✅ **Spring Boot** (should be pre-installed)
   - ✅ **Docker** (for container support)
   - ✅ **Cucumber for Java** (for BDD tests)

## Step 4: Configure Run Configurations

### Backend Application
1. **Run → Edit Configurations**
2. **Add New → Application**
3. **Configuration:**
   - **Name**: News Aggregator Backend
   - **Main class**: `com.newsaggregator.NewsAggregatorApplication`
   - **VM options**: `-Xmx1024m -Dspring.profiles.active=local`
   - **Environment variables**: 
     ```
     GUARDIAN_API_KEY=your_guardian_key
     NYTIMES_API_KEY=your_nytimes_key
     ```
   - **Working directory**: `c:\Java-microservice\news-aggregator`

### Unit Tests
1. **Add New → JUnit**
2. **Configuration:**
   - **Name**: All Unit Tests
   - **Test kind**: All in package
   - **Package**: `com.newsaggregator`
   - **Search for tests**: In whole project

### Integration Tests
1. **Add New → Maven**
2. **Configuration:**
   - **Name**: Integration Tests
   - **Command line**: `verify`
   - **Working directory**: `c:\Java-microservice\news-aggregator`

## Step 5: Environment Variables Setup

### Method 1: IntelliJ Run Configuration
1. **Run → Edit Configurations**
2. **Select your application configuration**
3. **Environment variables** → Click folder icon
4. **Add:**
   ```
   GUARDIAN_API_KEY=your_actual_guardian_key
   NYTIMES_API_KEY=your_actual_nytimes_key
   ```

### Method 2: System Environment Variables
1. **Windows Key + R** → type `sysdm.cpl`
2. **Advanced → Environment Variables**
3. **System Variables → New:**
   - Variable: `GUARDIAN_API_KEY`
   - Value: `your_actual_guardian_key`
4. **Repeat for NYTIMES_API_KEY**
5. **Restart IntelliJ**

## Step 6: Build and Test in IntelliJ

### Build Project
1. **Build → Build Project** (Ctrl+F9)
2. **Check for compilation errors** in Messages tab
3. **Resolve any dependency issues**

### Run Unit Tests
1. **Right-click** on `src/test/java`
2. **Run 'All Tests'**
3. **Verify all tests pass** in Test Results tab

### Run Application
1. **Click Run button** or press Shift+F10
2. **Check console output** for successful startup
3. **Verify** application starts on port 8080

## Step 7: Testing Workflow in IntelliJ

### 1. Unit Testing
```java
// Right-click on individual test files and run:
- NewsAggregatorServiceTest.java
- NewsControllerTest.java
- CacheServiceTest.java
- NewsAggregatorTest.java
```

### 2. Debug Mode
1. **Set breakpoints** in service methods
2. **Run in Debug mode** (Shift+F9)
3. **Step through code** to verify logic

### 3. HTTP Client Testing
1. **Tools → HTTP Client → Test RESTful Web Service**
2. **Test endpoints:**
   ```
   GET http://localhost:8080/api/v1/news/search?keyword=apple
   GET http://localhost:8080/actuator/health
   ```

## Step 8: Code Quality Checks

### Enable Inspections
1. **File → Settings → Editor → Inspections**
2. **Enable:**
   - Java → Probable bugs
   - Java → Performance issues
   - Spring → Spring Boot
   - General → Duplicated code

### Code Coverage
1. **Run → Run with Coverage**
2. **Select test configuration**
3. **View coverage report** in Coverage tab
4. **Target**: 90%+ coverage

## Step 9: Maven Integration

### Maven Tool Window
1. **View → Tool Windows → Maven**
2. **Available commands:**
   - `clean` - Clean build artifacts
   - `compile` - Compile source code
   - `test` - Run unit tests
   - `package` - Create JAR file
   - `spring-boot:run` - Run application

### Command Line in IntelliJ
1. **View → Tool Windows → Terminal**
2. **Run Maven commands:**
   ```bash
   ./mvnw clean test
   ./mvnw spring-boot:run
   ./mvnw package -DskipTests
   ```

## Step 10: Troubleshooting Common Issues

### Issue 1: Lombok Not Working
**Solution:**
1. Install Lombok plugin
2. **File → Settings → Build → Compiler → Annotation Processors**
3. **Enable annotation processing**
4. **Restart IntelliJ**

### Issue 2: Spring Boot Not Recognized
**Solution:**
1. **File → Project Structure → Facets**
2. **Add Spring facet** to main module
3. **Configure Spring Boot** in facet settings

### Issue 3: Tests Not Running
**Solution:**
1. **File → Settings → Build → Build Tools → Maven → Runner**
2. **Delegate IDE build/run actions to Maven**: Enable
3. **Reimport Maven project**

### Issue 4: Port Already in Use
**Solution:**
1. **Change port** in `application.yml`:
   ```yaml
   server:
     port: 8081
   ```
2. **Or kill process** using port 8080:
   ```bash
   netstat -ano | findstr :8080
   taskkill /PID <process_id> /F
   ```

## Step 11: Verification Checklist

### ✅ IntelliJ Setup Complete When:
- [ ] Project imports without errors
- [ ] Java 17 SDK configured
- [ ] All plugins installed
- [ ] Run configurations created
- [ ] Environment variables set
- [ ] Project builds successfully
- [ ] All unit tests pass
- [ ] Application starts on port 8080
- [ ] Health check endpoint responds
- [ ] API endpoints return data
- [ ] Code coverage > 90%
- [ ] No compilation warnings
- [ ] Swagger UI accessible

## Quick Start Commands

```bash
# In IntelliJ Terminal:
./mvnw clean test                    # Run all tests
./mvnw spring-boot:run              # Start application
./mvnw package -DskipTests          # Build JAR
java -jar target/news-aggregator-1.0.0.jar  # Run JAR
```

**🎯 Your IntelliJ environment is ready when all checklist items are complete and the application runs successfully with all tests passing!**