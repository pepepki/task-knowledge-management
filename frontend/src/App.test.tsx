import { render, screen } from '@testing-library/react';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import App from './App';

// axiosをモック化（APIを叩かないようにする）
vi.mock('axios');

describe('App Routing and Auth', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it('未ログイン時はログイン画面が表示されること', async () => {
    render(<App />);

    const loginHeading = await screen.findByRole('heading', { name: /ログイン/i });
    expect(loginHeading).toBeInTheDocument();

    const usernameInput = screen.getByLabelText(/ユーザー名/i);
    const passwordInput = screen.getByLabelText(/パスワード/i);

    expect(usernameInput).toBeInTheDocument();
    expect(passwordInput).toBeInTheDocument();

    const loginButton = screen.getByRole('button', { name: /ログイン/i });
    expect(loginButton).toBeInTheDocument();
  });
});