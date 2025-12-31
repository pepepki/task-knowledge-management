import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { Login } from './components/Login';
import { Signup } from './components/Signup';
import { TaskListPage } from './components/TaskListPage';
import './App.css';

function App() {
  const [authToken, setAuthToken] = useState<string | null>(localStorage.getItem('auth'));

  // アプリ起動時に、保存されている認証情報をaxiosにセットする
  useEffect(() => {
    if (authToken) {
      axios.defaults.headers.common['Authorization'] = authToken;
    }
  }, [authToken]);

  const handleLoginSuccess = (token: string) => {
    setAuthToken(token);
  };

  const handleLogout = () => {
    localStorage.removeItem('auth');
    delete axios.defaults.headers.common['Authorization'];
    setAuthToken(null);
  };

  return (
    <BrowserRouter>
      <div className="container">
        <header className="main-header">
          <h1>Task Manager</h1>
          {authToken && (
            <button onClick={handleLogout} className="logout-button">ログアウト</button>
          )}
        </header>

        <Routes>
          {/* ログイン・登録画面 */}
          <Route path="/login" element={<Login onLoginSuccess={handleLoginSuccess} />} />
          <Route path="/signup" element={<Signup />} />

          {/* 認証済みの場合のみタスク一覧を表示、そうでなければログインへ */}
          <Route 
            path="/" 
            element={authToken ? <TaskListPage /> : <Navigate to="/login" replace />} 
          />

          {/* 定義外のURLはルートへ飛ばす */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;