import { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

export const Signup = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/api/auth/signup', { username, password });
      alert('登録完了！ログインしてください。');
      navigate('/login');
    } catch (error) {
      console.error(error);
      alert('登録に失敗しました');
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2>ユーザー登録</h2>
        <form onSubmit={handleSignup} className="auth-form">
          <div className="form-group">
            <label>ユーザー名</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="ユーザー名を入力"
              required
            />
          </div>
          <div className="form-group">
            <label>パスワード</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="パスワードを入力"
              required
            />
          </div>
          <button type="submit">アカウント作成</button>
        </form>
        <div className="auth-footer">
          <p>すでにアカウントをお持ちですか？ <Link to="/login">ログイン</Link></p>
        </div>
      </div>
    </div>
  );
};