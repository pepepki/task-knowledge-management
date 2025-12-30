import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { expect, test, vi } from 'vitest';
import TaskForm from './TaskForm';
import axios from 'axios';

// axiosをモック化（実際に通信させない）
vi.mock('axios');
const mockedAxios = axios as any;

test('フォームに値を入力して送信すると、APIが呼ばれコールバックが実行されること', async () => {
  const mockOnTaskCreated = vi.fn();
  render(<TaskForm onTaskCreated={mockOnTaskCreated} />);

  const titleInput = screen.getByTestId('task-title-input');
  const descInput = screen.getByTestId('task-desc-input');
  const submitButton = screen.getByRole('button', { name: 'タスクを保存' });

  fireEvent.change(titleInput, { target: { value: '新しいテストタスク' } });
  fireEvent.change(descInput, { target: { value: 'テストの説明文です' } });

  mockedAxios.post.mockResolvedValue({ data: {} });

  fireEvent.click(submitButton);

  await waitFor(() => {
    expect(mockedAxios.post).toHaveBeenCalled();
  });

  expect(mockOnTaskCreated).toHaveBeenCalled();
});