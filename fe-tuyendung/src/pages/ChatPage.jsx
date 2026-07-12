import React, { useEffect, useState, useRef, useCallback } from 'react';
import { Input, Button, Spin, Badge, Avatar, Typography, Empty } from 'antd';
import { SendOutlined, UserOutlined, MessageOutlined } from '@ant-design/icons';
import { messageApi, adminCompanyApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';
import { useSearchParams } from 'react-router-dom';

const { Text } = Typography;
const GREEN = '#00b14f';
// WebSocket URL: dùng cùng host với trang nhưng port 8080 (backend trực tiếp)
const WS_BASE = `ws://${window.location.hostname}:8080/AppTuyenDung/ws/chat`;

function getInitials(name = '') {
  const w = name.trim().split(/\s+/);
  if (w.length >= 2) return (w[0][0] + w[1][0]).toUpperCase();
  return name.slice(0, 2).toUpperCase() || '?';
}

function avatarColor(id = 0) {
  const colors = ['#00b14f','#1677ff','#fa8c16','#722ed1','#f5222d'];
  return colors[id % colors.length];
}

export default function ChatPage() {
  const { user } = useAuth();
  const [searchParams] = useSearchParams();

  const [conversations, setConversations] = useState([]);  // recent list
  const [activeUserId, setActiveUserId] = useState(null);  // người đang chat
  const [activeUserName, setActiveUserName] = useState('');
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [histLoading, setHistLoading] = useState(false);
  const [employers, setEmployers] = useState([]);  // danh sách employer để bắt đầu chat mới

  const wsRef = useRef(null);
  const bottomRef = useRef(null);
  const myId = user?.userId;

  // Kết nối WebSocket
  useEffect(() => {
    if (!myId) return;
    const ws = new WebSocket(`${WS_BASE}/${myId}`);
    wsRef.current = ws;

    ws.onmessage = (e) => {
      try {
        const msg = JSON.parse(e.data);
        if (msg.type === 'chat') {
          setMessages((prev) => {
            // Tránh duplicate
            if (prev.some(m => m.id === msg.id)) return prev;
            return [...prev, msg];
          });
          // Cập nhật recent list
          setConversations((prev) => {
            const otherId = msg.senderId === myId ? msg.receiverId : msg.senderId;
            const otherName = msg.senderId === myId ? msg.receiverName : msg.senderName;
            const existing = prev.filter(c => c.otherId !== otherId);
            return [{ otherId, otherName, lastMsg: msg.content, sentAt: msg.sentAt, unread: msg.senderId !== myId && otherId !== activeUserId }, ...existing];
          });
        }
      } catch {}
    };

    ws.onerror = () => console.error('[WS] error');

    return () => { ws.close(); };
  }, [myId]);

  // Load recent conversations
  useEffect(() => {
    if (!myId) return;
    setLoading(true);
    messageApi.getRecent()
      .then((res) => {
        if (res.data.success) {
          const data = res.data.data || [];
          const convs = data.map((m) => {
            const otherId = m.senderId === myId ? m.receiverId : m.senderId;
            const otherName = m.senderId === myId ? m.receiverName : m.senderName;
            return { otherId, otherName, lastMsg: m.content, sentAt: m.sentAt, unread: !m.isRead && m.receiverId === myId };
          });
          setConversations(convs);
        }
      })
      .finally(() => setLoading(false));

    // Load danh sách employer/candidate để bắt đầu chat mới (dùng company list endpoint sẵn có)
    // Thực ra cần user list — dùng candidateList hay employerList
    // Dùng API đơn giản: lấy list từ jobs -> companyName liên kết employer
    // Cho đơn giản: load danh sách công ty từ adminCompanyApi (chứa tên)
    // FE sẽ tự build "chat với employer" từ job detail page
  }, [myId]);

  // Mở chat với userId từ query param ?with=X
  useEffect(() => {
    const withId = searchParams.get('with');
    const withName = searchParams.get('name');
    if (withId && withId !== String(activeUserId)) {
      openConversation(parseInt(withId), withName || 'Người dùng');
    }
  }, [searchParams]);

  const openConversation = useCallback((otherId, otherName) => {
    setActiveUserId(otherId);
    setActiveUserName(otherName);
    setHistLoading(true);
    setMessages([]);
    messageApi.getHistory(otherId)
      .then((res) => {
        if (res.data.success) setMessages(res.data.data || []);
      })
      .finally(() => setHistLoading(false));
    // Gửi read signal
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({ type: 'read', senderId: otherId }));
    }
    // Xóa unread badge
    setConversations(prev => prev.map(c => c.otherId === otherId ? { ...c, unread: false } : c));
  }, []);

  // Auto scroll to bottom
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const sendMessage = () => {
    const text = input.trim();
    if (!text || !activeUserId || wsRef.current?.readyState !== WebSocket.OPEN) return;
    wsRef.current.send(JSON.stringify({ type: 'chat', receiverId: activeUserId, content: text }));
    setInput('');
  };

  const handleKey = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); sendMessage(); }
  };

  return (
    <AppLayout>
      <div style={{ maxWidth: 1000, margin: '0 auto', padding: '20px 16px', height: 'calc(100vh - 80px)' }}>
        <div style={{
          display: 'flex', height: '100%', background: '#fff',
          borderRadius: 14, border: '1px solid #eee', overflow: 'hidden',
          boxShadow: '0 2px 16px rgba(0,0,0,0.07)',
        }}>

          {/* ── Sidebar: danh sách hội thoại ── */}
          <div style={{
            width: 280, flexShrink: 0, borderRight: '1px solid #f0f0f0',
            display: 'flex', flexDirection: 'column',
          }}>
            <div style={{ padding: '16px 16px 12px', borderBottom: '1px solid #f0f0f0' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <MessageOutlined style={{ color: GREEN, fontSize: 18 }} />
                <Text strong style={{ fontSize: 15 }}>Tin nhắn</Text>
              </div>
            </div>

            <div style={{ flex: 1, overflowY: 'auto' }}>
              {loading ? (
                <div style={{ textAlign: 'center', padding: 32 }}><Spin /></div>
              ) : conversations.length === 0 ? (
                <div style={{ padding: 24, textAlign: 'center', color: '#aaa', fontSize: 13 }}>
                  Chưa có hội thoại nào.<br />Vào trang việc làm và nhấn nhắn tin với nhà tuyển dụng.
                </div>
              ) : (
                conversations.map((c) => (
                  <div
                    key={c.otherId}
                    onClick={() => openConversation(c.otherId, c.otherName)}
                    style={{
                      display: 'flex', gap: 10, padding: '12px 14px', cursor: 'pointer',
                      background: activeUserId === c.otherId ? '#f0fdf4' : 'transparent',
                      borderLeft: activeUserId === c.otherId ? `3px solid ${GREEN}` : '3px solid transparent',
                      transition: 'background 0.15s',
                    }}
                    onMouseEnter={e => { if (activeUserId !== c.otherId) e.currentTarget.style.background = '#f9f9f9'; }}
                    onMouseLeave={e => { if (activeUserId !== c.otherId) e.currentTarget.style.background = 'transparent'; }}
                  >
                    <Avatar size={40} style={{ background: avatarColor(c.otherId), flexShrink: 0 }}>
                      {getInitials(c.otherName)}
                    </Avatar>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Text strong style={{ fontSize: 13, color: '#1a1a1a' }}>{c.otherName}</Text>
                        <Text style={{ fontSize: 11, color: '#bbb' }}>
                          {c.sentAt ? dayjs(c.sentAt).format('HH:mm') : ''}
                        </Text>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 2 }}>
                        <Text style={{
                          fontSize: 12, color: c.unread ? '#1a1a1a' : '#aaa', fontWeight: c.unread ? 600 : 400,
                          overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: 160,
                        }}>
                          {c.lastMsg || ''}
                        </Text>
                        {c.unread && <Badge dot style={{ background: GREEN }} />}
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* ── Main chat area ── */}
          {!activeUserId ? (
            <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', gap: 12, color: '#aaa' }}>
              <MessageOutlined style={{ fontSize: 48 }} />
              <Text style={{ color: '#bbb', fontSize: 14 }}>Chọn một cuộc trò chuyện để bắt đầu</Text>
            </div>
          ) : (
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
              {/* Header */}
              <div style={{
                padding: '14px 18px', borderBottom: '1px solid #f0f0f0',
                display: 'flex', alignItems: 'center', gap: 12,
              }}>
                <Avatar size={36} style={{ background: avatarColor(activeUserId) }}>
                  {getInitials(activeUserName)}
                </Avatar>
                <Text strong style={{ fontSize: 15 }}>{activeUserName}</Text>
                <div style={{
                  marginLeft: 'auto', width: 8, height: 8, borderRadius: '50%', background: GREEN,
                }} title="Đang hoạt động" />
              </div>

              {/* Messages */}
              <div style={{ flex: 1, overflowY: 'auto', padding: '16px 18px', display: 'flex', flexDirection: 'column', gap: 6 }}>
                {histLoading ? (
                  <div style={{ textAlign: 'center', padding: 40 }}><Spin /></div>
                ) : messages.length === 0 ? (
                  <div style={{ textAlign: 'center', color: '#ccc', fontSize: 13, marginTop: 40 }}>
                    Bắt đầu cuộc trò chuyện với {activeUserName}
                  </div>
                ) : (
                  messages.map((msg, i) => {
                    const isMine = msg.senderId === myId;
                    const showTime = i === 0 || dayjs(msg.sentAt).diff(dayjs(messages[i - 1]?.sentAt), 'minute') > 5;
                    return (
                      <React.Fragment key={msg.id || `${msg.sentAt}-${i}`}>
                        {showTime && (
                          <div style={{ textAlign: 'center', color: '#ccc', fontSize: 11, margin: '6px 0' }}>
                            {dayjs(msg.sentAt).format('DD/MM HH:mm')}
                          </div>
                        )}
                        <div style={{
                          display: 'flex', justifyContent: isMine ? 'flex-end' : 'flex-start',
                          alignItems: 'flex-end', gap: 6,
                        }}>
                          {!isMine && (
                            <Avatar size={26} style={{ background: avatarColor(activeUserId), flexShrink: 0, marginBottom: 2 }}>
                              {getInitials(activeUserName)}
                            </Avatar>
                          )}
                          <div style={{
                            maxWidth: '65%',
                            background: isMine ? GREEN : '#f0f0f0',
                            color: isMine ? '#fff' : '#1a1a1a',
                            borderRadius: isMine ? '18px 18px 4px 18px' : '18px 18px 18px 4px',
                            padding: '9px 14px',
                            fontSize: 14,
                            lineHeight: 1.5,
                            wordBreak: 'break-word',
                            boxShadow: '0 1px 2px rgba(0,0,0,0.08)',
                          }}>
                            {msg.content}
                          </div>
                        </div>
                      </React.Fragment>
                    );
                  })
                )}
                <div ref={bottomRef} />
              </div>

              {/* Input */}
              <div style={{
                padding: '12px 16px', borderTop: '1px solid #f0f0f0',
                display: 'flex', gap: 10, alignItems: 'flex-end',
              }}>
                <Input.TextArea
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  onKeyDown={handleKey}
                  placeholder="Nhập tin nhắn... (Enter để gửi)"
                  autoSize={{ minRows: 1, maxRows: 4 }}
                  style={{ borderRadius: 20, resize: 'none', paddingInline: 14 }}
                />
                <Button
                  type="primary"
                  icon={<SendOutlined />}
                  onClick={sendMessage}
                  disabled={!input.trim()}
                  style={{
                    background: GREEN, borderColor: GREEN, borderRadius: 20,
                    height: 38, width: 38, padding: 0, flexShrink: 0,
                  }}
                />
              </div>
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
