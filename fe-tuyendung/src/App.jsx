import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import viVN from 'antd/locale/vi_VN';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/vi';

import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';

import LoginPage from './pages/LoginPage';
import RegisterCandidatePage from './pages/RegisterCandidatePage';
import RegisterEmployerPage from './pages/RegisterEmployerPage';
import JobListPage from './pages/JobListPage';
import JobDetailPage from './pages/JobDetailPage';
import EmployerDashboardPage from './pages/EmployerDashboardPage';
import ApplicationManagePage from './pages/ApplicationManagePage';
import SkillManagePage from './pages/SkillManagePage';
import UploadCVPage from './pages/UploadCVPage';

dayjs.extend(relativeTime);
dayjs.locale('vi');

export default function App() {
  return (
    <ConfigProvider
      locale={viVN}
      theme={{
        token: {
          colorPrimary: '#00b14f',
          borderRadius: 8,
          fontFamily: "'Inter', 'Segoe UI', Roboto, sans-serif",
          colorSuccess: '#00b14f',
        },
      }}
    >
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register/candidate" element={<RegisterCandidatePage />} />
            <Route path="/register/employer" element={<RegisterEmployerPage />} />

            <Route path="/jobs" element={
              <PrivateRoute><JobListPage /></PrivateRoute>
            } />
            <Route path="/jobs/:id" element={
              <PrivateRoute><JobDetailPage /></PrivateRoute>
            } />
            <Route path="/cv/upload" element={
              <PrivateRoute roles={['CANDIDATE']}><UploadCVPage /></PrivateRoute>
            } />

            <Route path="/employer/dashboard" element={
              <PrivateRoute roles={['EMPLOYER']}><EmployerDashboardPage /></PrivateRoute>
            } />
            <Route path="/employer/applications" element={
              <PrivateRoute roles={['EMPLOYER']}><ApplicationManagePage /></PrivateRoute>
            } />
            <Route path="/employer/skills" element={
              <PrivateRoute roles={['EMPLOYER']}><SkillManagePage /></PrivateRoute>
            } />

            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ConfigProvider>
  );
}
