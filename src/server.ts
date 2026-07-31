import app from './app';
import { env } from './config/env';

const server = app.listen(env.PORT, () => {
  console.log(`🚀 Breaking Chains Backend running on http://localhost:${env.PORT}`);
  console.log(`Environment: ${env.NODE_ENV}`);
});

process.on('unhandledRejection', (err: Error) => {
  console.error('UNHANDLED REJECTION! 💥 Shutting down...', err);
  server.close(() => {
    process.exit(1);
  });
});
