import React, { useState } from 'react';
import axios from 'axios';

interface TaskFormProps {
    onTaskCreated: () => void; // 登録成功後に親コンポーネントを更新するための関数
}

const TaskForm: React.FC<TaskFormProps> = ({ onTaskCreated }) => {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            // バックエンドの POST API を叩く
            await axios.post('http://localhost:8080/api/tasks', {
                title,
                description
            });
            // 入力欄をクリア
            setTitle('');
            setDescription('');
            // 親コンポーネントに通知（一覧を再取得させる）
            onTaskCreated();
        } catch (error) {
            console.error('Error creating task:', error);
            alert('タスクの登録に失敗しました');
        }
    };

    return (
        <form onSubmit={handleSubmit} className="task-form-card">
            <h3 className="form-title">新規タスク登録</h3>

            <div className="form-group">
                <label htmlFor="task-title">タイトル</label>
                <input
                    id="task-title"
                    data-testid="task-title-input"
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    required
                />
            </div>

            <div className="form-group">
                <label htmlFor="task-desc">説明</label>
                <textarea
                    id="task-desc"
                    data-testid="task-desc-input"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
            </div>

            <div className="form-actions">
                <button type="submit" className="submit-button">タスクを保存</button>
            </div>
        </form>
    );
};

export default TaskForm;