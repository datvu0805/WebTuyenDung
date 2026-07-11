import React, { useEffect, useState } from 'react';
import {
  Table, Button, Modal, Form, Input, Space, message, Popconfirm, Typography
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, BankOutlined } from '@ant-design/icons';
import { adminCompanyApi } from '../api/services';
import AppLayout from '../components/AppLayout';

const { Title, Text } = Typography;
const GREEN = '#00b14f';

export default function AdminPage() {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    adminCompanyApi.getAll()
      .then((res) => { if (res.data.success) setCompanies(res.data.data || []); })
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
      if (res.data.success) {
        message.success(editing ? 'Cập nhật thành công' : 'Tạo công ty thành công');
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
      const res = await adminCompanyApi.delete(id);
      if (res.data.success) { message.success('Đã xóa công ty'); load(); }
      else message.error(res.data.message);
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: 'Tên công ty', dataIndex: 'companyName', key: 'companyName', render: (v) => <Text strong>{v}</Text> },
    { title: 'Mô tả', dataIndex: 'description', key: 'description', responsive: ['md'],
      render: (v) => v ? <Text ellipsis style={{ maxWidth: 300 }}>{v}</Text> : <Text type="secondary">—</Text>
    },
    { title: 'Ngày tạo', dataIndex: 'createdAt', key: 'createdAt', responsive: ['lg'], width: 160 },
    {
      title: 'Thao tác', key: 'actions', width: 100,
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
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #1677ff 0%, #0958d9 100%)', padding: '28px 24px 60px' }}>
        <div style={{ maxWidth: 1000, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>
            <BankOutlined style={{ marginRight: 8 }} />Quản trị hệ thống
          </Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Quản lý danh sách công ty</Text>
        </div>
      </div>

      <div style={{ maxWidth: 1000, margin: '-32px auto 0', padding: '0 16px 32px' }}>
        <div style={{ background: '#fff', borderRadius: 14, padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.07)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <Title level={5} style={{ margin: 0 }}>Danh sách công ty ({companies.length})</Title>
            <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
              style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
              Thêm công ty
            </Button>
          </div>
          <Table columns={columns} dataSource={companies} rowKey="id" loading={loading}
            pagination={{ pageSize: 15, showSizeChanger: false }} scroll={{ x: 600 }} />
        </div>
      </div>

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
    </AppLayout>
  );
}
