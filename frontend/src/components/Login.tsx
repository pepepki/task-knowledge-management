import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

interface LoginProps {
    onLoginSuccess: (token: string) => void;
}

export const Login = (props: LoginProps) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const authHeader = 'Basic ' + btoa(`${username}:${password}`);

            const response = await axios.get('http://localhost:8080/api/user/me', {
                headers: { Authorization: authHeader }
            });

            if (response.data.username) {
                // 1. ブラウザに保存
                localStorage.setItem('auth', authHeader);

                // 2. 親コンポーネント(App.tsx)の状態を更新
                props.onLoginSuccess(authHeader);

                // 3. メイン画面へ移動
                navigate('/');
            }
        } catch (error) {
            console.error('Login error:', error);
            alert('ログインに失敗しました。ユーザー名またはパスワードが正しくありません。');
        }
    };

    return (
        <div className="auth-container">
            <h2>ログイン</h2>
            <form onSubmit={handleLogin} className="auth-form">
                <div className="form-group">
                    <label htmlFor="username">ユーザー名</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="username">パスワード</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" className="login-button">ログイン</button>
            </form>
            <div className="auth-footer">
                <p>アカウントをお持ちでないですか？ <Link to="/signup">新規登録</Link></p>
            </div>
        </div>
    );
};