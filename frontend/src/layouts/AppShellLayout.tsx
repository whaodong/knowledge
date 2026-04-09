import { Outlet } from 'react-router-dom';
import { motion } from 'motion/react';
import { Sidebar } from '../components/layout/Sidebar';
import { Topbar } from '../components/layout/Topbar';

export function AppShellLayout(): JSX.Element {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <Topbar />
        <motion.main
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.2 }}
          className="flex-1 p-6"
        >
          <Outlet />
        </motion.main>
      </div>
    </div>
  );
}
