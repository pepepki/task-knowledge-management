import { useState, useEffect } from 'react';
import axios from 'axios';

interface Task {
  id: number;
  title: string;
  description: string;
  status: string;
}

export const TaskListPage = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  const fetchTasks = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/tasks');
      setTasks(response.data);
    } catch (error) {
      console.error('Fetch error:', error);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await axios.post('http://localhost:8080/api/tasks', { title, description });
    setTitle('');
    setDescription('');
    fetchTasks();
  };

  const handleToggle = async (id: number) => {
    await axios.patch(`http://localhost:8080/api/tasks/${id}/toggle`);
    fetchTasks();
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('削除しますか？')) return;
    await axios.delete(`http://localhost:8080/api/tasks/${id}`);
    fetchTasks();
  };

  return (
    <main>
      <section className="task-form">
        <h2>新しいタスクを追加</h2>
        <form onSubmit={handleSubmit}>
          <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="タイトル" required />
          <input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="説明" />
          <button type="submit">追加</button>
        </form>
      </section>

      <section className="task-list">
        <h2>タスク一覧</h2>
        <table>
          <thead>
            <tr><th>ID</th><th>タイトル</th><th>状態</th><th>操作</th></tr>
          </thead>
          <tbody>
            {Array.isArray(tasks) && tasks.map(task => (
              <tr key={task.id}>
                <td>{task.id}</td>
                <td className={task.status === 'DONE' ? 'strikethrough' : ''}>{task.title}</td>
                <td>{task.status}</td>
                <td>
                  <button onClick={() => handleToggle(task.id)}>
                    {task.status === 'DONE' ? '戻す' : '完了'}
                  </button>
                  <button onClick={() => handleDelete(task.id)} className="delete-btn">削除</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </main>
  );
};