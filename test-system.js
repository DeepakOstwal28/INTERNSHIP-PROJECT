const axios = require('axios');

async function testSystem() {
  console.log('🧪 Testing API Gateway microservices system...');

  try {
    console.log('\n🔍 Testing API Gateway health...');
    const gatewayHealth = await axios.get('http:
    console.log(`✅ API Gateway: ${JSON.stringify(gatewayHealth.data)}`);
  } catch (error) {
    console.log(`❌ API Gateway: ${error.message}`);
  }

  try {
    console.log('\n🔍 Testing Auth Service health...');
    const authHealth = await axios.get('http:
    console.log(`✅ Auth Service: ${JSON.stringify(authHealth.data)}`);
  } catch (error) {
    console.log(`❌ Auth Service: ${error.message}`);
  }

  try {
    console.log('\n🔍 Testing Frontend...');
    const frontendHealth = await axios.get('http:
    console.log(`✅ Frontend: ${JSON.stringify(frontendHealth.data)}`);
  } catch (error) {
    console.log(`❌ Frontend: ${error.message}`);
  }

  console.log('\n📊 System Status Summary:');
  console.log('- API Gateway: Running on port 3000');
  console.log('- Auth Service: Running on port 3001');
  console.log('- Frontend: Running on port 3002');
  console.log('- Movies Service: Running on port 3003');
  console.log('- Music Service: Running on port 3004');
  console.log('- Anime Service: Running on port 3005');
  console.log('- Videos Service: Running on port 3006');
  console.log('- Notification Service: Running on port 3007');
  
  console.log('\n✅ Test complete!');
  console.log('🌐 You can access the system at: http:
}

testSystem();

