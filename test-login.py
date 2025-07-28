import requests
import json

# 测试登录API
def test_login():
    url = "http://localhost/api/users/login"  # 通过nginx代理访问正确的登录端点
    
    # 测试数据
    login_data = {
        "username": "admin",
        "password": "admin123"
    }
    
    headers = {
        "Content-Type": "application/json"
    }
    
    try:
        print("正在测试登录...")
        print(f"URL: {url}")
        print(f"数据: {json.dumps(login_data, indent=2)}")
        
        response = requests.post(url, json=login_data, headers=headers)
        
        print(f"\n响应状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        
        print(f"响应内容: {response.text[:500]}...")  # 显示前500个字符
        
        if response.status_code == 200:
            try:
                result = response.json()
                print("登录成功!")
                print(f"响应数据: {json.dumps(result, indent=2, ensure_ascii=False)}")
            except:
                print("响应不是JSON格式，可能是HTML错误页面")
        else:
            print("登录失败!")
            try:
                error_data = response.json()
                print(f"错误信息: {json.dumps(error_data, indent=2, ensure_ascii=False)}")
            except:
                print(f"错误信息: {response.text[:500]}...")
                
    except requests.exceptions.ConnectionError:
        print("连接失败! 请确保nginx代理服务正在运行在 http://localhost")
    except Exception as e:
        print(f"测试过程中发生错误: {str(e)}")

if __name__ == "__main__":
    test_login()