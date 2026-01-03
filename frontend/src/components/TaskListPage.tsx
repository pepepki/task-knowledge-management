import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';


interface User {
  id: number;
  username: string;
}

interface Task {
  id: number;
  title: string;
  description: string;
  status: string;
  user: User;      // 作成者
  assignee?: User; // 担当者
}

export const TaskListPage = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [users, setUsers] = useState<any[]>([]);
  const [assigneeId, setAssigneeId] = useState<number | "">("");
  const navigate = useNavigate();

  // ステータスの表示名マッピング
  const STATUS_LABELS: Record<string, string> = {
    TODO: '未着手',
    ASSIGN_WAITING: 'アサイン待ち',
    PROGRESS: '対応中',
    IN_REVIEW: 'レビュー中',
    DONE: '完了',
    DELETED: '削除済み'
  };

  const fetchTasks = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/tasks');
      setTasks(response.data);
    } catch (error) {
      console.error('Fetch error:', error);
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/users');
      if (Array.isArray(response.data)) {
        setUsers(response.data);
      } else {
        console.error("想定外のデータ形式です:", response.data);
        setUsers([]); // 配列でない場合は空にする
      }
    } catch (error) {
      console.error("ユーザー一覧の取得に失敗しました", error);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('auth');
    if (!token) {
      navigate('/login'); // ログインしてなければ戻す
      return;
    }
    axios.defaults.headers.common['Authorization'] = token;
    fetchTasks();
    fetchUsers();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8080/api/tasks', {
        title,
        description,
        assigneeId: assigneeId === "" ? null : assigneeId // IDをバックエンドに送る
      });
      setTitle('');
      setDescription('');
      setAssigneeId("");
      fetchTasks(); // 一覧再取得
    } catch (error) {
      console.error("作成失敗", error);
    }
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
          <select
            value={assigneeId}
            onChange={(e) => setAssigneeId(e.target.value ? Number(e.target.value) : "")}
          >
            <option value="">選択してください</option>
            {Array.isArray(users) && users.map(u => (
              <option key={u.id} value={u.id}>{u.username}</option>
            ))}
          </select>
          <button type="submit">追加</button>
        </form>
      </section>

      <section className="task-list">
        <h2>タスク一覧</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>タイトル</th>
              <th>説明</th>
              <th>作成者</th>
              <th>担当者</th>
              <th>状態</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {tasks.map(task => (
              <tr key={task.id}>
                <td>{task.id}</td>
                <td className={task.status === 'DONE' ? 'strikethrough' : ''}>
                  {task.title}
                </td>
                <td className={task.status === 'DONE' ? 'strikethrough' : ''}>
                  {task.description}
                </td>
                <td>{task.user?.username}</td>
                <td>{task.assignee?.username || '-'}</td>
                <td>
                  <span className={`status-badge ${task.status.toLowerCase()}`}>
                    {STATUS_LABELS[task.status]}
                  </span>
                </td>
                <td>
                  <button onClick={() => handleToggle(task.id)}>
                    {task.status === 'DONE' ? '戻す' : '完了'}
                  </button>
                  <button className="delete-btn" onClick={() => handleDelete(task.id)}>
                    削除
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </main>
  );
};