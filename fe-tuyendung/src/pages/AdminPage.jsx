import React, { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, Space, message, Popconfirm, Typography, Upload, Tooltip, Tabs
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, BankOutlined,
  UploadOutlined, DownloadOutlined, ApartmentOutlined
} from '@ant-design/icons';
import { adminCompanyApi, jobPositionApi } from '../api/services';
import AppLayout from '../components/AppLayout';
import { downloadCompanyTemplate, downloadPositionTemplate } from '../utils/excelTemplates';

const { Title, Text } = Typography;
const GREEN = '#00b14f';
const BLUE = '#1677ff';

const tableHeaderStyle = {
  background: GREEN,
  color: '#fff',
  fontWeight: 700,
};

const positionHeaderStyle = {
  background: BLUE,
  color: '#fff',
  fontWeight: 700,
};

// ===================== TAB COMPANY =====================
function CompanyTab() {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [importing, setImporting] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    adminCompanyApi.getAll()
      .then(res => { if (res.data.success) setCompanies(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (item) => {
    setEditing(item);
    form.setFieldsValue({ companyName: item.companyName, description: item.description });
    setModalOpen(true);
  };

  const onSave = async (values) => {
    setSaving(true);
    try {
      const res = editing
        ? await adminCompanyApi.update({ ...values, id: editing.id })
        : await adminCompanyApi.create(values);
      if (res.data.success) { message.success(editing ? 'Cập nhật thành công' : 'Tạo công ty thành công'); setModalOpen(false); load(); }
      else message.error(res.data.message);
    } catch { message.error('Có lỗi xảy ra'); } finally { setSaving(false); }
  };

  const onDelete = async (id) => {
    try {
      const res = await adminCompanyApi.delete(id);
      if (res.data.success) { message.success('Đã xóa công ty'); load(); }
      else message.error(res.data.message);
    } catch { message.error('Xóa thất bại'); }
  };

  const handleImport = async ({ file }) => {
    setImporting(true);
    try {
      const fd = new FormData(); fd.append('file', file);
      const res = await adminCompanyApi.import(fd);
      if (res.data.success) { message.success(res.data.message); load(); }
      else message.error(res.data.message);
    } catch { message.error('Import thất bại'); } finally { setImporting(false); }
    return false;
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60, onHeaderCell: () => ({ style: tableHeaderStyle }) },
    { title: 'Tên công ty', dataIndex: 'companyName', key: 'companyName', onHeaderCell: () => ({ style: tableHeaderStyle }),
      render: v => <Text strong>{v}</Text> },
    { title: 'Mô tả', dataIndex: 'description', key: 'description', responsive: ['md'], onHeaderCell: () => ({ style: tableHeaderStyle }),
      render: v => v ? <Text ellipsis style={{ maxWidth: 300 }}>{v}</Text> : <Text type="secondary">—</Text> },
    { title: 'Ngày tạo', dataIndex: 'createdAt', key: 'createdAt', responsive: ['lg'], width: 160, onHeaderCell: () => ({ style: tableHeaderStyle }) },
    { title: 'Thao tác', key: 'actions', width: 100, onHeaderCell: () => ({ style: tableHeaderStyle }),
      render: (_, r) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(r)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa công ty này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap', gap: 8 }}>
        <Text strong style={{ fontSize: 15 }}>Danh sách công ty ({companies.length})</Text>
        <Space wrap>
          <Tooltip title="Tải file mẫu Excel">
            <Button icon={<DownloadOutlined />} onClick={() => downloadCompanyTemplate().catch(() => message.error('Tạo file mẫu thất bại'))}>
              File mẫu
            </Button>
          </Tooltip>
          <Upload showUploadList={false} accept=".xlsx,.xls" customRequest={handleImport} disabled={importing}>
            <Button icon={<UploadOutlined />} loading={importing}>Import Excel</Button>
          </Upload>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
            style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
            Thêm công ty
          </Button>
        </Space>
      </div>
      <Table columns={columns} dataSource={companies} rowKey="id" loading={loading}
        pagination={{ pageSize: 15, showSizeChanger: false }} scroll={{ x: 600 }}
        bordered size="middle" />

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa công ty' : 'Thêm công ty mới'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null} width={480}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="companyName" label={<span style={{ fontWeight: 600 }}>Tên công ty</span>}
            rules={[{ required: true, message: 'Nhập tên công ty' }]}>
            <Input placeholder="Tên công ty..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="description" label={<span style={{ fontWeight: 600 }}>Mô tả</span>}>
            <Input.TextArea rows={3} placeholder="Mô tả về công ty..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              {editing ? 'Lưu thay đổi' : 'Tạo công ty'}
            </Button>
          </div>
        </Form>
      </Modal>
    </>
  );
}

// ===================== TAB JOB POSITIONS =====================
function JobPositionTab() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [importing, setImporting] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    jobPositionApi.getAll()
      .then(res => { if (res.data.success) setList(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (item) => {
    setEditing(item);
    form.setFieldsValue({ name: item.name, description: item.description });
    setModalOpen(true);
  };

  const onSave = async (values) => {
    setSaving(true);
    try {
      const res = editing
        ? await jobPositionApi.update({ ...values, id: editing.id })
        : await jobPositionApi.create(values);
      if (res.data.success) { message.success(editing ? 'Cập nhật thành công' : 'Thêm thành công'); setModalOpen(false); load(); }
      else message.error(res.data.message);
    } catch { message.error('Có lỗi xảy ra'); } finally { setSaving(false); }
  };

  const onDelete = async (id) => {
    try {
      const res = await jobPositionApi.delete(id);
      if (res.data.success) { message.success('Đã xóa'); load(); }
      else message.error(res.data.message);
    } catch { message.error('Xóa thất bại'); }
  };

  const handleImport = async ({ file }) => {
    setImporting(true);
    try {
      const fd = new FormData(); fd.append('file', file);
      const res = await jobPositionApi.import(fd);
      if (res.data.success) { message.success(res.data.message); load(); }
      else message.error(res.data.message);
    } catch { message.error('Import thất bại'); } finally { setImporting(false); }
    return false;
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60, onHeaderCell: () => ({ style: positionHeaderStyle }) },
    { title: 'Chức danh', dataIndex: 'name', key: 'name', onHeaderCell: () => ({ style: positionHeaderStyle }),
      render: v => <Text strong>{v}</Text> },
    { title: 'Mô tả', dataIndex: 'description', key: 'description', responsive: ['md'], onHeaderCell: () => ({ style: positionHeaderStyle }),
      render: v => v ? <Text ellipsis style={{ maxWidth: 300 }}>{v}</Text> : <Text type="secondary">—</Text> },
    { title: 'Thao tác', key: 'actions', width: 100, onHeaderCell: () => ({ style: positionHeaderStyle }),
      render: (_, r) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(r)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa chức danh này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap', gap: 8 }}>
        <Text strong style={{ fontSize: 15 }}>Danh sách chức danh ({list.length})</Text>
        <Space wrap>
          <Tooltip title="Tải file mẫu Excel">
            <Button icon={<DownloadOutlined />} onClick={() => downloadPositionTemplate().catch(() => message.error('Tạo file mẫu thất bại'))}>
              File mẫu
            </Button>
          </Tooltip>
          <Upload showUploadList={false} accept=".xlsx,.xls" customRequest={handleImport} disabled={importing}>
            <Button icon={<UploadOutlined />} loading={importing}>Import Excel</Button>
          </Upload>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
            style={{ background: BLUE, borderColor: BLUE, borderRadius: 8, fontWeight: 600 }}>
            Thêm chức danh
          </Button>
        </Space>
      </div>
      <Table columns={columns} dataSource={list} rowKey="id" loading={loading}
        pagination={{ pageSize: 15, showSizeChanger: false }} scroll={{ x: 500 }}
        bordered size="middle" />

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa chức danh' : 'Thêm chức danh mới'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null} width={480}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="name" label={<span style={{ fontWeight: 600 }}>Tên chức danh</span>}
            rules={[{ required: true, message: 'Nhập tên chức danh' }]}>
            <Input placeholder="VD: Lập trình viên Backend..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="description" label={<span style={{ fontWeight: 600 }}>Mô tả</span>}>
            <Input.TextArea rows={3} placeholder="Mô tả ngắn về chức danh..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: BLUE, borderColor: BLUE, fontWeight: 600 }}>
              {editing ? 'Lưu thay đổi' : 'Thêm'}
            </Button>
          </div>
        </Form>
      </Modal>
    </>
  );
}

// ===================== MAIN PAGE =====================
export default function AdminPage() {
  const tabItems = [
    {
      key: 'companies',
      label: <span><BankOutlined /> Công ty</span>,
      children: <CompanyTab />,
    },
    {
      key: 'job-positions',
      label: <span><ApartmentOutlined /> Chức danh công việc</span>,
      children: <JobPositionTab />,
    },
  ];

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #1677ff 0%, #0958d9 100%)', padding: '28px 24px 60px' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>
            <BankOutlined style={{ marginRight: 8 }} />Quản trị hệ thống
          </Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Quản lý dữ liệu danh mục</Text>
        </div>
      </div>
      <div style={{ maxWidth: 1100, margin: '-32px auto 0', padding: '0 16px 32px' }}>
        <div style={{ background: '#fff', borderRadius: 14, padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.07)' }}>
          <Tabs defaultActiveKey="companies" items={tabItems} />
        </div>
      </div>
    </AppLayout>
  );
}
