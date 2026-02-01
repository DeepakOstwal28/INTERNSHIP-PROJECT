const http = require('http');

const services = [
  { name: 'API Gateway', port: 3000, path: '/health' },
  { name: 'Auth Service', port: 3001, path: '/health' },
  { name: 'Frontend', port: 3002, path: '/api/config' },
  { name: 'Movies Service', port: 3003, path: '/health' },
  { name: 'Music Service', port: 3004, path: '/health' },
  { name: 'Anime Service', port: 3005, path: '/health' },
  { name: 'Videos Service', port: 3006, path: '/health' },
  { name: 'Notification Service', port: 3007, path: '/health' },
  { name: 'Load Balancer', port: 3100, path: '/' }
];

async function checkService(name, port, path) {
  return new Promise((resolve) => {
    const req = http.get(`http://localhost:${port}${path}`, (res) => {
      let data = '';
      res.on('data', (chunk) => {
        data += chunk;
      });
      res.on('end', () => {
        resolve({
          name,
          status: 'online',
          statusCode: res.statusCode,
          message: `✅ ${name} is running on port ${port}`
        });
      });
    });
    
    req.on('error', (error) => {
      resolve({
        name,
        status: 'offline',
        message: `❌ ${name} is not running on port ${port}: ${error.message}`
      });
    });
    
    req.setTimeout(3000, () => {
      req.abort();
      resolve({
        name,
        status: 'timeout',
        message: `⚠️ ${name} timed out on port ${port}`
      });
    });
  });
}

async function checkAllServices() {
  console.log('🔍 Checking all API Gateway microservices...\n');
  
  const results = await Promise.all(
    services.map(service => checkService(service.name, service.port, service.path))
  );
  
  
  console.log('📊 Service Status Summary:');
  console.log('==========================');
  
  const online = results.filter(r => r.status === 'online');
  const offline = results.filter(r => r.status === 'offline' || r.status === 'timeout');
  
  results.forEach(result => {
    console.log(result.message);
  });
  
  console.log('\n📈 Statistics:');
  console.log(`Total services: ${services.length}`);
  console.log(`Online: ${online.length}`);
  console.log(`Offline: ${offline.length}`);
  
  if (online.length === services.length) {
    console.log('\n✅ All services are running!');
    console.log('🌐 Access the application at: http://localhost:3002/login.html');
  } else {
    console.log('\n⚠️ Some services are not running.');
    console.log('📝 Run NEW_START.bat to start all services.');
  }
}

checkAllServices();
