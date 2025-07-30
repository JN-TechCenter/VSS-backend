#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time
from datetime import datetime

# 配置
BASE_URL = "http://localhost:3002"
HEADERS = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
}

def log_message(message):
    """打印带时间戳的日志消息"""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{timestamp}] {message}")

def test_server_connectivity():
    """测试服务器连接性"""
    log_message("=== 测试服务器连接性 ===")
    
    try:
        # 测试健康检查端点
        response = requests.get(f"{BASE_URL}/actuator/health", timeout=5)
        log_message(f"健康检查状态: {response.status_code}")
        if response.status_code == 200:
            log_message(f"健康检查响应: {response.json()}")
            return True
        else:
            log_message(f"健康检查失败: {response.text}")
            return False
    except Exception as e:
        log_message(f"服务器连接失败: {str(e)}")
        return False

def test_registration_with_minimal_data():
    """测试最小数据注册"""
    log_message("=== 测试最小数据注册 ===")
    
    # 生成唯一用户名
    timestamp = int(time.time())
    username = f"testuser_{timestamp}"
    
    registration_data = {
        "username": username,
        "password": "Test123456",
        "email": f"{username}@test.com"
    }
    
    log_message(f"注册数据: {json.dumps(registration_data, indent=2)}")
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/users/register",
            headers=HEADERS,
            json=registration_data,
            timeout=10
        )
        
        log_message(f"响应状态码: {response.status_code}")
        log_message(f"响应头: {dict(response.headers)}")
        
        if response.status_code == 201:
            response_data = response.json()
            log_message(f"注册成功: {json.dumps(response_data, indent=2, ensure_ascii=False)}")
            return True, response_data
        else:
            log_message(f"注册失败: {response.text}")
            try:
                error_data = response.json()
                log_message(f"错误详情: {json.dumps(error_data, indent=2, ensure_ascii=False)}")
            except:
                pass
            return False, None
            
    except requests.exceptions.Timeout:
        log_message("请求超时")
        return False, None
    except requests.exceptions.ConnectionError as e:
        log_message(f"连接错误: {str(e)}")
        return False, None
    except Exception as e:
        log_message(f"请求异常: {str(e)}")
        return False, None

def test_registration_with_full_data():
    """测试完整数据注册"""
    log_message("=== 测试完整数据注册 ===")
    
    # 生成唯一用户名
    timestamp = int(time.time())
    username = f"fulluser_{timestamp}"
    
    registration_data = {
        "username": username,
        "password": "Test123456",
        "email": f"{username}@test.com",
        "fullName": "测试用户",
        "phoneNumber": "13800138000",
        "department": "测试部门",
        "role": "OBSERVER"
    }
    
    log_message(f"注册数据: {json.dumps(registration_data, indent=2, ensure_ascii=False)}")
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/users/register",
            headers=HEADERS,
            json=registration_data,
            timeout=10
        )
        
        log_message(f"响应状态码: {response.status_code}")
        log_message(f"响应头: {dict(response.headers)}")
        
        if response.status_code == 201:
            response_data = response.json()
            log_message(f"注册成功: {json.dumps(response_data, indent=2, ensure_ascii=False)}")
            return True, response_data
        else:
            log_message(f"注册失败: {response.text}")
            try:
                error_data = response.json()
                log_message(f"错误详情: {json.dumps(error_data, indent=2, ensure_ascii=False)}")
            except:
                pass
            return False, None
            
    except Exception as e:
        log_message(f"请求异常: {str(e)}")
        return False, None

def test_login_after_registration(username, password):
    """测试注册后登录"""
    log_message("=== 测试注册后登录 ===")
    
    login_data = {
        "username": username,
        "password": password
    }
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/users/login",
            headers=HEADERS,
            json=login_data,
            timeout=10
        )
        
        log_message(f"登录响应状态码: {response.status_code}")
        
        if response.status_code == 200:
            response_data = response.json()
            log_message(f"登录成功: {json.dumps(response_data, indent=2, ensure_ascii=False)}")
            return True, response_data.get('token')
        else:
            log_message(f"登录失败: {response.text}")
            return False, None
            
    except Exception as e:
        log_message(f"登录异常: {str(e)}")
        return False, None

def main():
    """主测试函数"""
    log_message("开始注册功能详细测试")
    
    # 1. 测试服务器连接性
    if not test_server_connectivity():
        log_message("服务器连接失败，终止测试")
        return
    
    # 2. 测试最小数据注册
    success, user_data = test_registration_with_minimal_data()
    if success and user_data:
        username = user_data.get('user', {}).get('username')
        if username:
            # 测试登录
            test_login_after_registration(username, "Test123456")
    
    # 3. 测试完整数据注册
    success, user_data = test_registration_with_full_data()
    if success and user_data:
        username = user_data.get('user', {}).get('username')
        if username:
            # 测试登录
            test_login_after_registration(username, "Test123456")
    
    log_message("测试完成")

if __name__ == "__main__":
    main()