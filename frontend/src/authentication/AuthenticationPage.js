import { useState } from 'react';
import {baseUrl} from "../GlobalContext";

function Login({updateIsAuth}) {
    const [username, setUsername] = useState(localStorage.getItem('username') || '');
    const [password, setPassword] = useState(localStorage.getItem('password') || '');

    const handleLogin = async () => {
        // Store username and password in localStorage for future requests
        localStorage.setItem('username', username);
        localStorage.setItem('password', password);

        try {
            const response = await fetch(baseUrl + 'api/v1/players', {
                method: "get",
                headers: new Headers({
                    "ngrok-skip-browser-warning": true,
                    "Authorization": "Basic " + btoa(localStorage.getItem("username") + ":" + localStorage.getItem("password"))
                }),
            });
            await response.json();
            updateIsAuth(true);
            alert('Credentials saved!');
        } catch (error) {
            console.error('Error fetching players:', error);
            alert('Credentials wrong!');
            updateIsAuth(false);
        }
  
  
       
    };

    return (
        <div style={{ maxWidth: '300px', margin: '0 auto', padding: '20px', border: '1px solid #ccc', borderRadius: '5px' }}>
            <h3>Login</h3>
            <div style={{ marginBottom: '10px' }}>
                <label>Username:</label>
                <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                />
            </div>
            <div style={{ marginBottom: '10px' }}>
                <label>Password:</label>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                />
            </div>
            <button onClick={handleLogin} style={{ width: '100%', padding: '10px', background: '#007bff', color: '#fff', border: 'none', borderRadius: '5px' }}>
                Save Credentials
            </button>
        </div>
    );
}

export default Login;
