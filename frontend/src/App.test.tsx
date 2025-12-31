import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import App from './App';

describe('App Routing and Auth', () => {
  it('未ログイン時はログイン画面が表示されること', () => {
    // LocalStorageをクリアして未ログイン状態を保証
    localStorage.clear();
    
    render(<App />);

    // 「ログイン」という見出しが存在するか確認
    // (Login.tsxで <h2>ログイン</h2> としている場合)
    const loginHeading = screen.getByRole('heading', { name: /ログイン/i });
    expect(loginHeading).toBeDefined();
    
    // ユーザー名とパスワードの入力フィールドがあるか確認
    expect(screen.getByPlaceholderText(/ユーザー名/i)).toBeDefined();
    expect(screen.getByPlaceholderText(/パスワード/i)).toBeDefined();
  });

  it('新規登録へのリンクが存在すること', () => {
    render(<App />);
    const signupLink = screen.getByRole('link', { name: /新規登録/i });
    expect(signupLink).toBeDefined();
  });
});