#!/usr/bin/env python3
"""
用户注册功能测试脚本
"""

import requests
import json
import time

# 配置
BASE_URL = "http://localhost:3002"

def test_user_registration():
    """测试用户注册功能"""
    print("🧪 测试用户注册功能...")
    
    # 测试数据
    test_users = [
        {
            "username": "testuser1",
            "password": "password123",
            "email": "test1@example.com",
            "fullName": "测试用户1",
            "phoneNumber": "13800138001",
            "department": "技术部",
            "role": "OBSERVER"
        },
        {
            "username": "testuser2",
            "password": "password456",
            "email": "test2@example.com",
            "fullName": "测试用户2",
            "phoneNumber": "13800138002",
            "department": "运营部",
            "role": "OPERATOR"
        }
    ]
    
    for i, user_data in enumerate(test_users, 1):
        print(f"\n📝 测试注册用户 {i}: {user_data['username']}")
        
        try:
            url = f"{BASE_URL}/api/users/register"
            headers = {
                'Content-Type': 'application/json'
            }
            
            response = requests.post(url, json=user_data, headers=headers, timeout=10)
            
            print(f"   状态码: {response.status_code}")
            print(f"   响应头: {dict(response.headers)}")
            
            if response.status_code == 201:
                print("   ✅ 注册成功")
                try:
                    data = response.json()
                    print(f"   响应数据: {json.dumps(data, indent=2, ensure_ascii=False)}")
                except:
                    print(f"   响应文本: {response.text}")
            elif response.status_code == 400:
                print("   ❌ 注册失败 - 请求错误")
                try:
                    error_data = response.json()
                    print(f"   错误信息: {json.dumps(error_data, indent=2, ensure_ascii=False)}")
                except:
                    print(f"   错误文本: {response.text}")
            elif response.status_code == 401:
                print("   ⚠️  需要身份验证")
            elif response.status_code == 500:
                print("   ❌ 服务器内部错误")
                print(f"   响应文本: {response.text}")
            else:
                print(f"   ⚠️  其他状态: {response.status_code}")
                print(f"   响应文本: {response.text}")
                
        except requests.exceptions.ConnectionError:
            print(f"   ❌ 连接失败 - 服务器可能未启动")
        except requests.exceptions.Timeout:
            print(f"   ❌ 请求超时")
        except Exception as e:
            print(f"   ❌ 错误: {e}")

def test_duplicate_registration():
    """测试重复注册"""
    print("\n🔄 测试重复注册...")
    
    user_data = {
        "username": "testuser1",
        "password": "password123",
        "email": "test1@example.com",
        "fullName": "测试用户1",
        "phoneNumber": "13800138001",
        "department": "技术部",
        "role": "OBSERVER"
    }
    
    try:
        url = f"{BASE_URL}/api/users/register"
        headers = {'Content-Type': 'application/json'}
        
        response = requests.post(url, json=user_data, headers=headers, timeout=10)
        
        print(f"   状态码: {response.status_code}")
        
        if response.status_code == 400:
            print("   ✅ 正确拒绝重复注册")
            try:
                error_data = response.json()
                print(f"   错误信息: {json.dumps(error_data, indent=2, ensure_ascii=False)}")
            except:
                print(f"   错误文本: {response.text}")
        else:
            print(f"   ⚠️  意外状态: {response.status_code}")
            print(f"   响应: {response.text}")
            
    except Exception as e:
        print(f"   ❌ 错误: {e}")

def test_invalid_data():
    """测试无效数据"""
    print("\n❌ 测试无效数据...")
    
    invalid_cases = [
        {
            "name": "缺少用户名",
            "data": {
                "password": "password123",
                "email": "test@example.com",
                "fullName": "测试用户"
            }
        },
        {
            "name": "缺少密码",
            "data": {
                "username": "testuser3",
                "email": "test3@example.com",
                "fullName": "测试用户3"
            }
        },
        {
            "name": "无效邮箱",
            "data": {
                "username": "testuser4",
                "password": "password123",
                "email": "invalid-email",
                "fullName": "测试用户4"
            }
        }
    ]
    
    for case in invalid_cases:
        print(f"\n   测试: {case['name']}")
        
        try:
            url = f"{BASE_URL}/api/users/register"
            headers = {'Content-Type': 'application/json'}
            
            response = requests.post(url, json=case['data'], headers=headers, timeout=10)
            
            print(f"     状态码: {response.status_code}")
            
            if response.status_code == 400:
                print("     ✅ 正确拒绝无效数据")
            else:
                print(f"     ⚠️  意外状态: {response.status_code}")
                
            try:
                data = response.json()
                print(f"     响应: {json.dumps(data, indent=2, ensure_ascii=False)}")
            except:
                print(f"     响应文本: {response.text}")
                
        except Exception as e:
            print(f"     ❌ 错误: {e}")

def test_login_after_registration():
    """测试注册后登录"""
    print("\n🔐 测试注册后登录...")
    
    login_data = {
        "username": "testuser1",
        "password": "password123"
    }
    
    try:
        url = f"{BASE_URL}/api/users/login"
        headers = {'Content-Type': 'application/json'}
        
        response = requests.post(url, json=login_data, headers=headers, timeout=10)
        
        print(f"   状态码: {response.status_code}")
        
        if response.status_code == 200:
            print("   ✅ 登录成功")
            try:
                data = response.json()
                print(f"   登录响应: {json.dumps(data, indent=2, ensure_ascii=False)}")
            except:
                print(f"   响应文本: {response.text}")
        else:
            print(f"   ❌ 登录失败: {response.status_code}")
            print(f"   响应: {response.text}")
            
    except Exception as e:
        print(f"   ❌ 错误: {e}")

def main():
    print("=" * 60)
    print("🚀 用户注册功能测试")
    print("=" * 60)
    
    # 测试注册
    test_user_registration()
    
    # 测试重复注册
    test_duplicate_registration()
    
    # 测试无效数据
    test_invalid_data()
    
    # 测试登录
    test_login_after_registration()
    
    print("\n" + "=" * 60)
    print("✅ 测试完成")
    print("=" * 60)

if __name__ == "__main__":
    main()