import { useEffect, useState } from 'react'
import axios from 'axios'

// データの型を定義（TypeScript）
interface Task {
  id: number;
  title: string;
  description: string;
  status: string;
}

function App() {
  const [tasks, setTasks] = useState<Task[]>([]); // タスクを保存する変数
  const [loading, setLoading] = useState(true);   // 読み込み中フラグ

  useEffect(() => {
    // 画面が開いたときにバックエンドからデータを取得
    axios.get('http://localhost:8080/api/tasks')
      .then(response => {
        setTasks(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error("エラーが発生しました:", error);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>読み込み中...</div>;

  return (
    <div style={{ padding: '20px' }}>
      <h1>タスク一覧 (Sprint 1)</h1>
      <ul>
        {tasks.map(task => (
          <li key={task.id} style={{ marginBottom: '10px', borderBottom: '1px solid #ccc' }}>
            <h3>{task.title}</h3>
            <p>{task.description}</p>
            <span>状態: <b>{task.status}</b></span>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default App