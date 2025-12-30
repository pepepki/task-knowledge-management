import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { expect, test, vi } from 'vitest';
import App from './App';
import axios from 'axios';

vi.mock('axios');
const mockedAxios = axios as any;

test('削除ボタンを押すと confirm が表示され API が呼ばれること', async () => {
  // window.confirm をモック化して「OK」を返すようにする
  const confirmSpy = vi.spyOn(window, 'confirm').mockImplementation(() => true);
  
  // 既存のタスクがある状態でレンダリング
  mockedAxios.get.mockResolvedValue({
    data: [{ id: 1, title: '消されるタスク', description: 'desc', status: 'TODO' }]
  });

  render(<App />);

  // 削除ボタンを探してクリック
  const deleteBtn = await screen.findByText('削除');
  fireEvent.click(deleteBtn);

  expect(confirmSpy).toHaveBeenCalled();
  expect(mockedAxios.delete).toHaveBeenCalledWith('http://localhost:8080/api/tasks/1');
});