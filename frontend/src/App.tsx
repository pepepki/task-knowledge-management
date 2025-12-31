import { useEffect, useState } from 'react'
import axios from 'axios'
import TaskForm from './components/TaskForm'
import './App.css'

interface Task {
  id: number
  title: string
  description: string
  status: string
}

function App() {
  const [tasks, setTasks] = useState<Task[]>([])

  // タスク一覧を取得する関数
  const fetchTasks = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/tasks')
      setTasks(response.data)
    } catch (error) {
      console.error('Error fetching tasks:', error)
    }
  }

  const handleToggle = async (id: number) => {
    try {
      await axios.patch(`http://localhost:8080/api/tasks/${id}/toggle`);
      fetchTasks(); // 一覧を再取得
    } catch (error) {
      console.error('Error toggling task:', error);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('本当に削除しますか？')) return;
    try {
      await axios.delete(`http://localhost:8080/api/tasks/${id}`);
      fetchTasks(); // 一覧を再取得
    } catch (error) {
      console.error('Error deleting task:', error);
    }
  };

  // axiosのデフォルト設定に認証情報を含める
  axios.defaults.withCredentials = true;

  // Basic認証用のヘッダーを一時的に追加（開発用テスト）
  axios.defaults.headers.common['Authorization'] = 'Basic ' + btoa('user:password');

  useEffect(() => {
    fetchTasks()
  }, [])


  return (
    <div className="container">
      <header className="app-header">
        <h1>Task Management</h1>
        <span className="badge">Sprint 3</span>
      </header>

      <TaskForm onTaskCreated={fetchTasks} />

      <section className="list-section">
        <h2>タスク一覧</h2>
        <div className="table-wrapper">
          <table className="task-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>タイトル</th>
                <th>説明</th>
                <th>ステータス</th>
              </tr>
            </thead>
            <tbody>
              {tasks.map(task => (
                <tr key={task.id}>
                  <td>{task.id}</td>
                  <td className={task.status === 'DONE' ? 'strikethrough' : ''}>{task.title}</td>
                  <td>{task.description}</td>
                  <td>
                    <span className={`status-tag ${task.status.toLowerCase()}`}>{task.status}</span>
                  </td>
                  <td>
                    <button onClick={() => handleToggle(task.id)} className="action-button">
                      {task.status === 'DONE' ? '戻す' : '完了'}
                    </button>
                    <button onClick={() => handleDelete(task.id)} className="delete-button">削除</button>
                  </td>
                </tr>
              ))}            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}

export default App