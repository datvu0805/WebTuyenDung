import React, { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, Space, message, Popconfirm, Typography, Upload, Tooltip, Tabs, Tag, Switch
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, BankOutlined,
  UploadOutlined, DownloadOutlined, ApartmentOutlined, BulbOutlined, RobotOutlined,
  SafetyCertificateOutlined, ReadOutlined,
} from '@ant-design/icons';
import { adminCompanyApi, jobPositionApi, skillApi, adminSettingsApi, certificateApi, educationLevelApi } from '../api/services';
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

// ===================== TAB SKILL =====================
const SKILL_TAG_COLORS = ['blue', 'geekblue', 'purple', 'cyan', 'teal', 'volcano', 'orange'];

function SkillTab() {
  const [skills, setSkills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    skillApi.getAll()
      .then((res) => { if (res.data.success) setSkills(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (skill) => { setEditing(skill); form.setFieldsValue({ skillName: skill.skillName }); setModalOpen(true); };

  const onSave = async ({ skillName }) => {
    setSaving(true);
    try {
      const res = editing ? await skillApi.update(editing.id, skillName) : await skillApi.create(skillName);
      if (res.data.success) {
        message.success(editing ? 'Cập nhật thành công' : 'Thêm thành công');
        setModalOpen(false);
        load();
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Có lỗi xảy ra');
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async (id) => {
    try {
      await skillApi.delete(id);
      message.success('Đã xóa kỹ năng');
      load();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const columns = [
    {
      title: 'Kỹ năng', dataIndex: 'skillName', key: 'skillName', onHeaderCell: () => ({ style: tableHeaderStyle }),
      render: (v, _, idx) => (
        <Tag color={SKILL_TAG_COLORS[idx % SKILL_TAG_COLORS.length]} style={{ borderRadius: 6, padding: '3px 10px', fontSize: 13 }}>
          {v}
        </Tag>
      )
    },
    { title: 'ID', dataIndex: 'id', key: 'id', width: 70, onHeaderCell: () => ({ style: tableHeaderStyle }),
      render: (v) => <Text style={{ color: '#bbb', fontSize: 13 }}>#{v}</Text> },
    {
      title: 'Thao tác', key: 'actions', width: 100, align: 'right', onHeaderCell: () => ({ style: tableHeaderStyle }),
      render: (_, record) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(record)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa kỹ năng này?" onConfirm={() => onDelete(record.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap', gap: 8 }}>
        <Text strong style={{ fontSize: 15 }}>Danh sách kỹ năng ({skills.length})</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
          style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
          Thêm kỹ năng
        </Button>
      </div>
      <Table columns={columns} dataSource={skills} rowKey="id" loading={loading}
        pagination={{ pageSize: 15, showSizeChanger: false }} bordered size="middle" />

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa kỹ năng' : 'Thêm kỹ năng mới'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="skillName" label={<span style={{ fontWeight: 600 }}>Tên kỹ năng</span>}
            rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input placeholder="Java, ReactJS, Python..." size="large" style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              Lưu
            </Button>
          </div>
        </Form>
      </Modal>
    </>
  );
}

// ===================== TAB CERTIFICATE =====================
function CertificateTab() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    certificateApi.getAll()
      .then((res) => { if (res.data.success) setList(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (item) => { setEditing(item); form.setFieldsValue(item); setModalOpen(true); };

  const onSave = async (values) => {
    setSaving(true);
    try {
      const res = editing
        ? await certificateApi.update({ ...values, id: editing.id })
        : await certificateApi.create(values);
      if (res.data.success) {
        message.success(editing ? 'Cập nhật thành công' : 'Thêm thành công');
        setModalOpen(false);
        load();
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Có lỗi xảy ra');
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async (id) => {
    try {
      await certificateApi.delete(id);
      message.success('Đã xóa');
      load();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60, onHeaderCell: () => ({ style: tableHeaderStyle }) },
    { title: 'Tên chứng chỉ', dataIndex: 'certificateName', key: 'certificateName', onHeaderCell: () => ({ style: tableHeaderStyle }) },
    { title: 'Loại điểm', dataIndex: 'scoreType', key: 'scoreType', onHeaderCell: () => ({ style: tableHeaderStyle }) },
    {
      title: 'Thao tác', key: 'actions', width: 100, onHeaderCell: () => ({ style: tableHeaderStyle }),
      render: (_, r) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(r)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa chứng chỉ này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Text strong style={{ fontSize: 15 }}>Danh sách chứng chỉ ({list.length})</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
          style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
          Thêm chứng chỉ
        </Button>
      </div>
      <Table columns={columns} dataSource={list} rowKey="id" loading={loading}
        pagination={{ pageSize: 15, showSizeChanger: false }} bordered size="middle" />

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa chứng chỉ' : 'Thêm chứng chỉ'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null} width={460}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="certificateName" label={<span style={{ fontWeight: 600 }}>Tên chứng chỉ</span>}
            rules={[{ required: true, message: 'Nhập tên chứng chỉ' }]}>
            <Input placeholder="IELTS, TOEIC, AWS..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="scoreType" label={<span style={{ fontWeight: 600 }}>Loại điểm</span>}>
            <Input placeholder="Band, Score, Level..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              {editing ? 'Lưu thay đổi' : 'Thêm'}
            </Button>
          </div>
        </Form>
      </Modal>
    </>
  );
}

// ===================== TAB EDUCATION LEVEL =====================
function EducationLevelTab() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    educationLevelApi.getAll()
      .then((res) => { if (res.data.success) setList(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (item) => { setEditing(item); form.setFieldsValue({ levelName: item.levelName }); setModalOpen(true); };

  const onSave = async ({ levelName }) => {
    setSaving(true);
    try {
      const res = editing ? await educationLevelApi.update(editing.id, levelName) : await educationLevelApi.create(levelName);
      if (res.data.success) {
        message.success(editing ? 'Cập nhật thành công' : 'Thêm thành công');
        setModalOpen(false);
        load();
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Có lỗi xảy ra');
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async (id) => {
    try {
      await educationLevelApi.delete(id);
      message.success('Đã xóa');
      load();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60, onHeaderCell: () => ({ style: positionHeaderStyle }) },
    { title: 'Trình độ học vấn', dataIndex: 'levelName', key: 'levelName', onHeaderCell: () => ({ style: positionHeaderStyle }),
      render: v => <Text strong>{v}</Text> },
    {
      title: 'Thao tác', key: 'actions', width: 100, onHeaderCell: () => ({ style: positionHeaderStyle }),
      render: (_, r) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(r)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa trình độ này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Text strong style={{ fontSize: 15 }}>Danh sách trình độ học vấn ({list.length})</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
          style={{ background: BLUE, borderColor: BLUE, borderRadius: 8, fontWeight: 600 }}>
          Thêm trình độ
        </Button>
      </div>
      <Table columns={columns} dataSource={list} rowKey="id" loading={loading}
        pagination={{ pageSize: 15, showSizeChanger: false }} bordered size="middle" />

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa trình độ học vấn' : 'Thêm trình độ học vấn'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="levelName" label={<span style={{ fontWeight: 600 }}>Tên trình độ</span>}
            rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input placeholder="Đại học, Thạc sĩ..." size="large" style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: BLUE, borderColor: BLUE, fontWeight: 600 }}>
              Lưu
            </Button>
          </div>
        </Form>
      </Modal>
    </>
  );
}

// ===================== TAB SETTINGS =====================
function SettingsTab() {
  const [aiEnabled, setAiEnabled] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const load = () => {
    setLoading(true);
    adminSettingsApi.getAll()
      .then((res) => {
        if (res.data.success) {
          const setting = (res.data.data || []).find(s => s.key === 'ai_recommendation_enabled');
          setAiEnabled(setting?.value === 'true');
        }
      })
      .catch(() => message.error('Tải cấu hình thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const onToggleAi = async (checked) => {
    setSaving(true);
    try {
      const res = await adminSettingsApi.update('ai_recommendation_enabled', String(checked));
      if (res.data.success) {
        setAiEnabled(checked);
        message.success(checked ? 'Đã bật gợi ý AI' : 'Đã tắt gợi ý AI');
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Có lỗi xảy ra');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '40px 0' }}><Text type="secondary">Đang tải...</Text></div>;

  return (
    <div style={{ display: 'flex', alignItems: 'flex-start', gap: 16, padding: '8px 4px', maxWidth: 520 }}>
      <div style={{ width: 44, height: 44, borderRadius: 10, background: '#f0f5ff', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
        <RobotOutlined style={{ color: BLUE, fontSize: 20 }} />
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12 }}>
          <div>
            <Text strong style={{ fontSize: 15, display: 'block' }}>Gợi ý việc làm bằng AI</Text>
            <Text type="secondary" style={{ fontSize: 13 }}>
              Khi bật, hệ thống sẽ gọi AI service (Python) để gợi ý job dựa trên kỹ năng, mô tả công việc
              và mức lương mong muốn. Khi tắt, hệ thống dùng logic tính điểm thông thường theo kỹ năng và
              mức lương.
            </Text>
          </div>
          <Switch checked={aiEnabled} loading={saving} onChange={onToggleAi} />
        </div>
      </div>
    </div>
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
    {
      key: 'skills',
      label: <span><BulbOutlined /> Kỹ năng</span>,
      children: <SkillTab />,
    },
    {
      key: 'certificates',
      label: <span><SafetyCertificateOutlined /> Chứng chỉ</span>,
      children: <CertificateTab />,
    },
    {
      key: 'education-levels',
      label: <span><ReadOutlined /> Học vấn</span>,
      children: <EducationLevelTab />,
    },
    {
      key: 'settings',
      label: <span><RobotOutlined /> Cấu hình hệ thống</span>,
      children: <SettingsTab />,
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
