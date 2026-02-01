const mongoose = require('mongoose');


const MONGODB_URI = 'mongodb+srv:

async function testMongoDBConnection() {
  console.log('🔄 Testing MongoDB Atlas connection...');
  
  try {
    await mongoose.connect(MONGODB_URI, {
      serverSelectionTimeoutMS: 5000,
      connectTimeoutMS: 5000
    });
    
    console.log('✅ MongoDB Atlas connected successfully!');
    
    
    const collections = await mongoose.connection.db.listCollections().toArray();
    console.log(`\n📊 Database contains ${collections.length} collections:`);
    collections.forEach((collection, index) => {
      console.log(`${index + 1}. ${collection.name}`);
    });
    
    
    try {
      const usersCollection = mongoose.connection.db.collection('users');
      const userCount = await usersCollection.countDocuments();
      console.log(`\n👤 User collection contains ${userCount} users`);
      
      if (userCount > 0) {
        const users = await usersCollection.find({}).limit(5).toArray();
        console.log('📋 Sample users:');
        users.forEach(user => {
          console.log(`- ${user.username || user.email} (${user.role})`);
        });
      }
    } catch (err) {
      console.log('⚠️ Could not access users collection:', err.message);
    }
    
  } catch (error) {
    console.error('❌ MongoDB connection failed:', error.message);
    console.log('\n⚠️ Using in-memory fallback will be activated when running the application');
  } finally {
    await mongoose.disconnect();
    console.log('\n✅ Test complete');
  }
}

testMongoDBConnection();

