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

  useEffect(() => {
    fetchTasks()
  }, [])

  return (
    <div className="container">
      <header className="app-header">
        <h1>Task Management</h1>
        <span className="badge">Sprint 2</span>
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
                  <td className="task-title">{task.title}</td>
                  <td>{task.description}</td>
                  <td>
                    <span className={`status-tag ${task.status.toLowerCase()}`}>
                      {task.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}

export default App