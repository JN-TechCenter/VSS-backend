#!/bin/bash

# VSS Backend 测试运行脚本
# 用于运行单元测试和集成测试

echo "=========================================="
echo "VSS Backend 测试运行脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 函数：打印带颜色的消息
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# 函数：检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        print_message $RED "错误: $1 命令未找到，请确保已安装"
        exit 1
    fi
}

# 函数：运行单元测试
run_unit_tests() {
    print_message $BLUE "正在运行单元测试..."
    
    if [ -f "pom.xml" ]; then
        # Maven项目
        print_message $YELLOW "检测到Maven项目，使用Maven运行测试"
        mvn clean test -Dtest="*Test" -DfailIfNoTests=false
    elif [ -f "build.gradle" ] || [ -f "build.gradle.kts" ]; then
        # Gradle项目
        print_message $YELLOW "检测到Gradle项目，使用Gradle运行测试"
        ./gradlew clean test --tests "*Test"
    else
        print_message $RED "错误: 未找到Maven或Gradle构建文件"
        exit 1
    fi
    
    if [ $? -eq 0 ]; then
        print_message $GREEN "✅ 单元测试执行成功"
    else
        print_message $RED "❌ 单元测试执行失败"
        exit 1
    fi
}

# 函数：运行集成测试
run_integration_tests() {
    print_message $BLUE "正在运行集成测试..."
    
    if [ -f "pom.xml" ]; then
        # Maven项目
        mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false
    elif [ -f "build.gradle" ] || [ -f "build.gradle.kts" ]; then
        # Gradle项目
        ./gradlew test --tests "*IntegrationTest"
    fi
    
    if [ $? -eq 0 ]; then
        print_message $GREEN "✅ 集成测试执行成功"
    else
        print_message $RED "❌ 集成测试执行失败"
        exit 1
    fi
}

# 函数：生成测试报告
generate_test_report() {
    print_message $BLUE "正在生成测试报告..."
    
    if [ -f "pom.xml" ]; then
        # Maven项目 - 生成JaCoCo报告
        mvn jacoco:report
        if [ -d "target/site/jacoco" ]; then
            print_message $GREEN "✅ 测试覆盖率报告已生成: target/site/jacoco/index.html"
        fi
    elif [ -f "build.gradle" ] || [ -f "build.gradle.kts" ]; then
        # Gradle项目 - 生成JaCoCo报告
        ./gradlew jacocoTestReport
        if [ -d "build/reports/jacoco/test/html" ]; then
            print_message $GREEN "✅ 测试覆盖率报告已生成: build/reports/jacoco/test/html/index.html"
        fi
    fi
}

# 函数：显示帮助信息
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -u, --unit          只运行单元测试"
    echo "  -i, --integration   只运行集成测试"
    echo "  -a, --all           运行所有测试 (默认)"
    echo "  -r, --report        生成测试报告"
    echo "  -h, --help          显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0                  # 运行所有测试"
    echo "  $0 -u               # 只运行单元测试"
    echo "  $0 -i               # 只运行集成测试"
    echo "  $0 -a -r            # 运行所有测试并生成报告"
}

# 函数：检查测试环境
check_test_environment() {
    print_message $BLUE "检查测试环境..."
    
    # 检查Java版本
    if command -v java &> /dev/null; then
        java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        print_message $GREEN "Java版本: $java_version"
    else
        print_message $RED "错误: 未找到Java"
        exit 1
    fi
    
    # 检查构建工具
    if [ -f "pom.xml" ]; then
        check_command "mvn"
        mvn_version=$(mvn -version | head -n 1)
        print_message $GREEN "$mvn_version"
    elif [ -f "build.gradle" ] || [ -f "build.gradle.kts" ]; then
        if [ -f "./gradlew" ]; then
            print_message $GREEN "Gradle Wrapper 已找到"
        else
            check_command "gradle"
        fi
    fi
    
    # 检查测试目录
    if [ -d "tests" ]; then
        unit_tests=$(find tests/unit -name "*.java" 2>/dev/null | wc -l)
        integration_tests=$(find tests/integration -name "*.java" 2>/dev/null | wc -l)
        print_message $GREEN "发现 $unit_tests 个单元测试文件"
        print_message $GREEN "发现 $integration_tests 个集成测试文件"
    fi
}

# 主函数
main() {
    local run_unit=false
    local run_integration=false
    local run_all=true
    local generate_report=false
    
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -u|--unit)
                run_unit=true
                run_all=false
                shift
                ;;
            -i|--integration)
                run_integration=true
                run_all=false
                shift
                ;;
            -a|--all)
                run_all=true
                shift
                ;;
            -r|--report)
                generate_report=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                print_message $RED "未知选项: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # 检查测试环境
    check_test_environment
    
    echo ""
    print_message $BLUE "开始执行测试..."
    
    # 执行测试
    if [ "$run_all" = true ]; then
        run_unit_tests
        echo ""
        run_integration_tests
    elif [ "$run_unit" = true ]; then
        run_unit_tests
    elif [ "$run_integration" = true ]; then
        run_integration_tests
    fi
    
    # 生成报告
    if [ "$generate_report" = true ]; then
        echo ""
        generate_test_report
    fi
    
    echo ""
    print_message $GREEN "=========================================="
    print_message $GREEN "所有测试执行完成！"
    print_message $GREEN "=========================================="
}

# 运行主函数
main "$@"