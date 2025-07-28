@echo off
setlocal enabledelayedexpansion

REM VSS Backend 测试运行脚本 (Windows版本)
REM 用于运行单元测试和集成测试

echo ==========================================
echo VSS Backend 测试运行脚本
echo ==========================================

REM 设置变量
set RUN_UNIT=false
set RUN_INTEGRATION=false
set RUN_ALL=true
set GENERATE_REPORT=false

REM 解析命令行参数
:parse_args
if "%~1"=="" goto start_tests
if "%~1"=="-u" (
    set RUN_UNIT=true
    set RUN_ALL=false
    shift
    goto parse_args
)
if "%~1"=="--unit" (
    set RUN_UNIT=true
    set RUN_ALL=false
    shift
    goto parse_args
)
if "%~1"=="-i" (
    set RUN_INTEGRATION=true
    set RUN_ALL=false
    shift
    goto parse_args
)
if "%~1"=="--integration" (
    set RUN_INTEGRATION=true
    set RUN_ALL=false
    shift
    goto parse_args
)
if "%~1"=="-a" (
    set RUN_ALL=true
    shift
    goto parse_args
)
if "%~1"=="--all" (
    set RUN_ALL=true
    shift
    goto parse_args
)
if "%~1"=="-r" (
    set GENERATE_REPORT=true
    shift
    goto parse_args
)
if "%~1"=="--report" (
    set GENERATE_REPORT=true
    shift
    goto parse_args
)
if "%~1"=="-h" goto show_help
if "%~1"=="--help" goto show_help

echo 未知选项: %~1
goto show_help

:show_help
echo 用法: %~nx0 [选项]
echo.
echo 选项:
echo   -u, --unit          只运行单元测试
echo   -i, --integration   只运行集成测试
echo   -a, --all           运行所有测试 (默认)
echo   -r, --report        生成测试报告
echo   -h, --help          显示此帮助信息
echo.
echo 示例:
echo   %~nx0                  # 运行所有测试
echo   %~nx0 -u               # 只运行单元测试
echo   %~nx0 -i               # 只运行集成测试
echo   %~nx0 -a -r            # 运行所有测试并生成报告
goto end

:start_tests
echo 检查测试环境...

REM 检查Java版本
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java
    exit /b 1
)

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
    set JAVA_VERSION=!JAVA_VERSION:"=!
)
echo Java版本: !JAVA_VERSION!

REM 检查构建工具
if exist "pom.xml" (
    mvn -version >nul 2>&1
    if errorlevel 1 (
        echo 错误: 未找到Maven
        exit /b 1
    )
    echo 检测到Maven项目
    set BUILD_TOOL=maven
) else if exist "build.gradle" (
    if exist "gradlew.bat" (
        echo 检测到Gradle项目 (使用Wrapper)
        set BUILD_TOOL=gradle
    ) else (
        gradle -version >nul 2>&1
        if errorlevel 1 (
            echo 错误: 未找到Gradle
            exit /b 1
        )
        echo 检测到Gradle项目
        set BUILD_TOOL=gradle
    )
) else (
    echo 错误: 未找到Maven或Gradle构建文件
    exit /b 1
)

REM 统计测试文件
if exist "tests\unit" (
    for /f %%i in ('dir /b /s "tests\unit\*.java" 2^>nul ^| find /c /v ""') do set UNIT_COUNT=%%i
) else (
    set UNIT_COUNT=0
)

if exist "tests\integration" (
    for /f %%i in ('dir /b /s "tests\integration\*.java" 2^>nul ^| find /c /v ""') do set INTEGRATION_COUNT=%%i
) else (
    set INTEGRATION_COUNT=0
)

echo 发现 !UNIT_COUNT! 个单元测试文件
echo 发现 !INTEGRATION_COUNT! 个集成测试文件

echo.
echo 开始执行测试...

REM 执行测试
if "%RUN_ALL%"=="true" (
    call :run_unit_tests
    if errorlevel 1 exit /b 1
    echo.
    call :run_integration_tests
    if errorlevel 1 exit /b 1
) else if "%RUN_UNIT%"=="true" (
    call :run_unit_tests
    if errorlevel 1 exit /b 1
) else if "%RUN_INTEGRATION%"=="true" (
    call :run_integration_tests
    if errorlevel 1 exit /b 1
)

REM 生成报告
if "%GENERATE_REPORT%"=="true" (
    echo.
    call :generate_test_report
)

echo.
echo ==========================================
echo 所有测试执行完成！
echo ==========================================
goto end

:run_unit_tests
echo 正在运行单元测试...

if "%BUILD_TOOL%"=="maven" (
    echo 使用Maven运行单元测试
    mvn clean test -Dtest="*Test" -DfailIfNoTests=false
) else if "%BUILD_TOOL%"=="gradle" (
    echo 使用Gradle运行单元测试
    if exist "gradlew.bat" (
        gradlew.bat clean test --tests "*Test"
    ) else (
        gradle clean test --tests "*Test"
    )
)

if errorlevel 1 (
    echo ❌ 单元测试执行失败
    exit /b 1
) else (
    echo ✅ 单元测试执行成功
)
goto :eof

:run_integration_tests
echo 正在运行集成测试...

if "%BUILD_TOOL%"=="maven" (
    echo 使用Maven运行集成测试
    mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false
) else if "%BUILD_TOOL%"=="gradle" (
    echo 使用Gradle运行集成测试
    if exist "gradlew.bat" (
        gradlew.bat test --tests "*IntegrationTest"
    ) else (
        gradle test --tests "*IntegrationTest"
    )
)

if errorlevel 1 (
    echo ❌ 集成测试执行失败
    exit /b 1
) else (
    echo ✅ 集成测试执行成功
)
goto :eof

:generate_test_report
echo 正在生成测试报告...

if "%BUILD_TOOL%"=="maven" (
    mvn jacoco:report
    if exist "target\site\jacoco\index.html" (
        echo ✅ 测试覆盖率报告已生成: target\site\jacoco\index.html
    )
) else if "%BUILD_TOOL%"=="gradle" (
    if exist "gradlew.bat" (
        gradlew.bat jacocoTestReport
    ) else (
        gradle jacocoTestReport
    )
    if exist "build\reports\jacoco\test\html\index.html" (
        echo ✅ 测试覆盖率报告已生成: build\reports\jacoco\test\html\index.html
    )
)
goto :eof

:end
endlocal