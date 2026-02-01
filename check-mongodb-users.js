const axios = require('axios');

async function checkUsers() {
    try {
        const loginRes = await axios.post('http://localhost:3001/auth/login', {
            username: 'admin@example.com',
            password: 'admin123'
        });
        
        const token = loginRes.data.tokens?.accessToken || loginRes.data.accessToken;
        console.log('Logged in as admin');
        
        const usersRes = await axios.get('http://localhost:3001/auth/admin/users', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        console.log('\nMongoDB Users:');
        console.log('Total:', usersRes.data.users.length);
        console.log('\nUser List:');
        usersRes.data.users.forEach(user => {
            console.log(`- ${user.username} (${user.email}) - Role: ${user.role} - Permissions: [${(user.assignedMicroservices || []).join(', ')}]`);
        });
    } catch (error) {
        console.error('Error:', error.response?.data || error.message);
    }
}

checkUsers();
