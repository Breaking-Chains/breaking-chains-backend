import React, { useState, useEffect } from 'react';
import { 
  Shield, CheckCircle, AlertTriangle, Flame, Heart, BookOpen, User, Key, RefreshCw, 
  Trash2, Plus, PhoneCall, Award, DollarSign, Clock, Lock, Sparkles, Send, Copy, LogOut, MessageSquare
} from 'lucide-react';

const API_BASE_URL = 'http://localhost:8080';

export default function App() {
  const [activeTab, setActiveTab] = useState('auth');
  const [healthStatus, setHealthStatus] = useState(null);
  const [accessToken, setAccessToken] = useState(localStorage.getItem('bc_accessToken') || '');
  const [refreshToken, setRefreshToken] = useState(localStorage.getItem('bc_refreshToken') || '');
  const [userProfile, setUserProfile] = useState(null);
  
  // Console logs & responses
  const [apiResponse, setApiResponse] = useState(null);
  const [loading, setLoading] = useState(false);

  // Chains state
  const [chains, setChains] = useState([]);
  const [selectedChainId, setSelectedChainId] = useState('');
  
  // Form states
  const [authEmail, setAuthEmail] = useState('alex.smith@example.com');
  const [authPassword, setAuthPassword] = useState('Password123!');
  const [authFullName, setAuthFullName] = useState('Alex Smith');
  const [authUsername, setAuthUsername] = useState('alexsmith');

  // PMO Chain Creation Default State
  const [chainTitle, setChainTitle] = useState('Pure Path - Overcome PMO');
  const [chainDesc, setChainDesc] = useState('Breaking PMO addiction through daily spiritual purification and urge surfing.');
  const [chainCategory, setChainCategory] = useState('SPIRITUAL_MORAL');
  const [chainSubCategory, setChainSubCategory] = useState('PMO_RECOVERY');
  const [chainPrivacy, setChainPrivacy] = useState('LEVEL_2_FULL_COUNSEL');
  const [chainCost, setChainCost] = useState(5.0);
  const [chainTime, setChainTime] = useState(45);
  const [chainTriggers, setChainTriggers] = useState('Late Night in Bed, Boredom, Social Media Scrolling');
  const [chainSubstitute, setChainSubstitute] = useState("Perform Wudu with cool water, pray 2 Raka'at Salat al-Tawbah, and do 2-minute box breathing");
  const [chainIntent, setChainIntent] = useState('I intend for the sake of Allah to purify my eyes, heart, and soul.');

  // Check-In Form
  const [checkInStatus, setCheckInStatus] = useState('CLEAN');
  const [checkInIntensity, setCheckInIntensity] = useState(2);
  const [checkInTrigger, setCheckInTrigger] = useState('Late Night in Bed');
  const [checkInNote, setCheckInNote] = useState('Resisted urge by doing Wudu and box breathing.');
  const [checkInDeed, setCheckInDeed] = useState('Read 2 pages of Quran');
  const [logs, setLogs] = useState([]);

  // Emergency SOS Form
  const [sosType, setSosType] = useState('PHYSICAL_CIRCUIT_BREAKER');
  const [sosCravingBefore, setSosCravingBefore] = useState(9);
  const [activeSosSession, setActiveSosSession] = useState(null);
  const [sosCravingAfter, setSosCravingAfter] = useState(3);
  const [sosDuration, setSosDuration] = useState(180);
  const [sosTechnique, setSosTechnique] = useState('PHYSICAL_LEAVE_ROOM + WUDU_COOL_WATER + BOX_BREATHING_60S');

  // Partner Form & 2-Way Chat
  const [inviteRole, setInviteRole] = useState('SPIRITUAL_MENTOR');
  const [inviteCodeInput, setInviteCodeInput] = useState('');
  const [generatedInviteCode, setGeneratedInviteCode] = useState('');
  const [partnershipIdInput, setPartnershipIdInput] = useState('');
  const [counselNoteContent, setCounselNoteContent] = useState("Remember Allah's mercy is greater than any mistake. Read Surah Ad-Duha tonight and stay strong.");
  const [chatMessageContent, setChatMessageContent] = useState("Assalamu Alaikum, I completed my 7-day clean streak today!");
  const [chatMessages, setChatMessages] = useState([]);

  // Analytics
  const [analytics, setAnalytics] = useState(null);
  const [milestones, setMilestones] = useState([]);

  // Check Health on mount
  useEffect(() => {
    checkHealth();
  }, []);

  const saveTokens = (accToken, refToken) => {
    setAccessToken(accToken);
    setRefreshToken(refToken);
    if (accToken) localStorage.setItem('bc_accessToken', accToken);
    else localStorage.removeItem('bc_accessToken');
    if (refToken) localStorage.setItem('bc_refreshToken', refToken);
    else localStorage.removeItem('bc_refreshToken');
  };

  const makeApiCall = async (endpoint, method = 'GET', body = null, requireAuth = true) => {
    setLoading(true);
    setApiResponse(null);
    try {
      const headers = {
        'Content-Type': 'application/json'
      };

      if (requireAuth && accessToken) {
        headers['Authorization'] = `Bearer ${accessToken}`;
      }

      const options = {
        method,
        headers
      };

      if (body && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
        options.body = JSON.stringify(body);
      }

      const res = await fetch(`${API_BASE_URL}${endpoint}`, options);
      const data = await res.json();

      setApiResponse({
        status: res.status,
        statusText: res.statusText,
        requestId: res.headers.get('X-Request-ID'),
        data
      });

      setLoading(false);
      return { ok: res.ok, status: res.status, data };
    } catch (err) {
      setApiResponse({
        status: 500,
        statusText: 'Network Error',
        data: { status: 'error', message: err.message || 'Failed to connect to backend server at http://localhost:8080' }
      });
      setLoading(false);
      return { ok: false, status: 500, data: null };
    }
  };

  const checkHealth = async () => {
    const res = await makeApiCall('/health', 'GET', null, false);
    if (res.ok) {
      setHealthStatus(res.data);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    const res = await makeApiCall('/api/v1/auth/register', 'POST', {
      email: authEmail,
      password: authPassword,
      fullName: authFullName,
      username: authUsername
    }, false);

    if (res.ok && res.data?.data?.tokens) {
      saveTokens(res.data.data.tokens.accessToken, res.data.data.tokens.refreshToken);
      setUserProfile(res.data.data.user);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    const res = await makeApiCall('/api/v1/auth/login', 'POST', {
      email: authEmail,
      password: authPassword
    }, false);

    if (res.ok && res.data?.data?.tokens) {
      saveTokens(res.data.data.tokens.accessToken, res.data.data.tokens.refreshToken);
      setUserProfile(res.data.data.user);
    }
  };

  const handleFetchProfile = async () => {
    const res = await makeApiCall('/api/v1/users/me', 'GET');
    if (res.ok && res.data?.data?.user) {
      setUserProfile(res.data.data.user);
    }
  };

  const handleCreateChain = async (e) => {
    e.preventDefault();
    const triggersArray = chainTriggers.split(',').map(t => t.trim()).filter(Boolean);
    const res = await makeApiCall('/api/v1/chains', 'POST', {
      title: chainTitle,
      description: chainDesc,
      category: chainCategory,
      subCategory: chainSubCategory,
      privacyLevel: chainPrivacy,
      costPerInstance: parseFloat(chainCost),
      timeMinutesPerInstance: parseInt(chainTime),
      triggerTags: triggersArray,
      substituteAction: chainSubstitute,
      intentStatement: chainIntent
    });

    if (res.ok && res.data?.data?.id) {
      setSelectedChainId(res.data.data.id);
      handleFetchChains();
    }
  };

  const handleFetchChains = async () => {
    const res = await makeApiCall('/api/v1/chains', 'GET');
    if (res.ok && Array.isArray(res.data?.data)) {
      setChains(res.data.data);
      if (res.data.data.length > 0 && !selectedChainId) {
        setSelectedChainId(res.data.data[0].id);
      }
    }
  };

  const handleLogCheckIn = async (e) => {
    e.preventDefault();
    if (!selectedChainId) return alert('Please select or create a habit chain first.');
    const res = await makeApiCall(`/api/v1/chains/${selectedChainId}/logs`, 'POST', {
      status: checkInStatus,
      intensityLevel: parseInt(checkInIntensity),
      triggerTag: checkInTrigger,
      reflectionNote: checkInNote,
      goodDeedDone: checkInDeed
    });

    if (res.ok) {
      handleFetchLogs();
    }
  };

  const handleFetchLogs = async () => {
    if (!selectedChainId) return;
    const res = await makeApiCall(`/api/v1/chains/${selectedChainId}/logs`, 'GET');
    if (res.ok && Array.isArray(res.data?.data)) {
      setLogs(res.data.data);
    }
  };

  const handleStartSos = async () => {
    if (!selectedChainId) return alert('Please select or create a habit chain first.');
    const res = await makeApiCall('/api/v1/emergency/start', 'POST', {
      chainId: selectedChainId,
      sessionType: sosType,
      cravingBefore: parseInt(sosCravingBefore)
    });

    if (res.ok && res.data?.data) {
      setActiveSosSession(res.data.data);
    }
  };

  const handleCompleteSos = async () => {
    if (!activeSosSession?.sessionId) return;
    const res = await makeApiCall(`/api/v1/emergency/${activeSosSession.sessionId}/complete`, 'POST', {
      cravingAfter: parseInt(sosCravingAfter),
      durationSeconds: parseInt(sosDuration),
      techniqueUsed: sosTechnique
    });

    if (res.ok) {
      setActiveSosSession(null);
    }
  };

  const handleGenerateInvite = async () => {
    if (!selectedChainId) return alert('Please select or create a habit chain first.');
    const res = await makeApiCall(`/api/v1/chains/${selectedChainId}/partners/invite`, 'POST', {
      role: inviteRole
    });

    if (res.ok && res.data?.data?.inviteCode) {
      setGeneratedInviteCode(res.data.data.inviteCode);
      setInviteCodeInput(res.data.data.inviteCode);
      if (res.data.data.partnershipId) {
        setPartnershipIdInput(res.data.data.partnershipId);
      }
    }
  };

  const handleAcceptInvite = async () => {
    if (!inviteCodeInput) return alert('Please enter an invite code.');
    const res = await makeApiCall('/api/v1/partners/accept', 'POST', {
      inviteCode: inviteCodeInput
    });

    if (res.ok && res.data?.data?.partnershipId) {
      setPartnershipIdInput(res.data.data.partnershipId);
    }
  };

  const handleCreateCounselNote = async () => {
    if (!selectedChainId) return alert('Please select or create a habit chain first.');
    await makeApiCall(`/api/v1/chains/${selectedChainId}/counsel-notes`, 'POST', {
      noteContent: counselNoteContent
    });
  };

  const handleSendChatMessage = async () => {
    if (!partnershipIdInput) return alert('Please enter or generate a Partnership ID first.');
    const res = await makeApiCall(`/api/v1/partnerships/${partnershipIdInput}/messages`, 'POST', {
      messageContent: chatMessageContent
    });

    if (res.ok) {
      handleFetchChatMessages();
    }
  };

  const handleFetchChatMessages = async () => {
    if (!partnershipIdInput) return alert('Please enter or generate a Partnership ID first.');
    const res = await makeApiCall(`/api/v1/partnerships/${partnershipIdInput}/messages`, 'GET');
    if (res.ok && Array.isArray(res.data?.data)) {
      setChatMessages(res.data.data);
    }
  };

  const handleFetchAnalytics = async () => {
    if (!selectedChainId) return alert('Please select or create a habit chain first.');
    const res = await makeApiCall(`/api/v1/chains/${selectedChainId}/analytics`, 'GET');
    if (res.ok && res.data?.data) {
      setAnalytics(res.data.data);
    }
  };

  const handleFetchMilestones = async () => {
    const res = await makeApiCall('/api/v1/milestones', 'GET');
    if (res.ok && Array.isArray(res.data?.data)) {
      setMilestones(res.data.data);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 font-sans p-4 md:p-8">
      {/* Header */}
      <header className="max-w-6xl mx-auto flex flex-col md:flex-row justify-between items-center pb-6 mb-8 border-b border-slate-800 gap-4">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-purple-900/40 border border-purple-500/30 rounded-xl text-purple-400">
            <Shield className="w-8 h-8" />
          </div>
          <div>
            <h1 className="text-2xl font-bold bg-gradient-to-r from-purple-400 to-emerald-400 bg-clip-text text-transparent">
              Breaking Chains API Test Studio
            </h1>
            <p className="text-sm text-slate-400">
              Spring Boot 3 + Java 17 Backend Live Interactive Inspector
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3 flex-wrap">
          {healthStatus ? (
            <span className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-emerald-950/80 border border-emerald-500/30 text-emerald-400 text-xs font-semibold">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              Backend {healthStatus.status} ({healthStatus.framework})
            </span>
          ) : (
            <button onClick={checkHealth} className="px-3 py-1.5 rounded-full bg-rose-950/80 border border-rose-500/30 text-rose-400 text-xs font-semibold flex items-center gap-1.5">
              <AlertTriangle className="w-3.5 h-3.5" /> Check Backend Connection
            </button>
          )}

          {accessToken ? (
            <button onClick={() => saveTokens('', '')} className="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs flex items-center gap-1.5">
              <LogOut className="w-3.5 h-3.5" /> Logout
            </button>
          ) : (
            <span className="px-3 py-1.5 rounded-lg bg-amber-950/80 border border-amber-500/30 text-amber-400 text-xs font-medium">
              Unauthenticated
            </span>
          )}
        </div>
      </header>

      {/* Main Grid Layout */}
      <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8">
        
        {/* Left Column: API Modules & Tabs */}
        <main className="lg:col-span-7 space-y-6">
          
          {/* Active Chain Selector Bar */}
          {chains.length > 0 && (
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex flex-col sm:flex-row items-center justify-between gap-4">
              <div className="flex items-center gap-2">
                <Flame className="w-5 h-5 text-amber-400" />
                <span className="text-sm font-semibold text-slate-300">Active Habit Chain:</span>
              </div>
              <select 
                value={selectedChainId} 
                onChange={(e) => setSelectedChainId(e.target.value)}
                className="bg-slate-950 border border-slate-700 rounded-lg px-3 py-1.5 text-sm text-slate-200 focus:outline-none focus:border-purple-500 w-full sm:w-auto"
              >
                {chains.map(c => (
                  <option key={c.id} value={c.id}>
                    {c.title} ({c.subCategory})
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Module Tabs */}
          <nav className="flex gap-2 border-b border-slate-800 overflow-x-auto pb-2">
            {[
              { id: 'auth', label: '1. Auth & Users', icon: User },
              { id: 'chains', label: '2. PMO & Chains', icon: Shield },
              { id: 'checkin', label: '3. Check-In & Tawbah', icon: CheckCircle },
              { id: 'sos', label: '4. Emergency SOS', icon: PhoneCall },
              { id: 'counsel', label: '5. Mentorship & 2-Way Chat', icon: Heart },
              { id: 'analytics', label: '6. Analytics & Barakah', icon: Award }
            ].map(tab => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-sm font-medium transition-all whitespace-nowrap ${
                    isActive 
                      ? 'bg-purple-600 text-white shadow-lg shadow-purple-900/30' 
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  {tab.label}
                </button>
              );
            })}
          </nav>

          {/* TAB 1: AUTH & USER PROFILE */}
          {activeTab === 'auth' && (
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 space-y-6">
              <h2 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                <User className="w-5 h-5 text-purple-400" /> Authentication & User Profile APIs
              </h2>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <form onSubmit={handleRegister} className="space-y-3 bg-slate-950 p-4 rounded-xl border border-slate-800">
                  <h3 className="text-sm font-semibold text-purple-400">POST /api/v1/auth/register</h3>
                  <input type="email" placeholder="Email" value={authEmail} onChange={e => setAuthEmail(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  <input type="text" placeholder="Full Name" value={authFullName} onChange={e => setAuthFullName(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  <input type="text" placeholder="Username" value={authUsername} onChange={e => setAuthUsername(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  <input type="password" placeholder="Password" value={authPassword} onChange={e => setAuthPassword(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  <button type="submit" className="w-full bg-purple-600 hover:bg-purple-500 py-2 rounded-lg text-xs font-semibold">Register New User</button>
                </form>

                <form onSubmit={handleLogin} className="space-y-3 bg-slate-950 p-4 rounded-xl border border-slate-800">
                  <h3 className="text-sm font-semibold text-emerald-400">POST /api/v1/auth/login</h3>
                  <input type="email" placeholder="Email" value={authEmail} onChange={e => setAuthEmail(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  <input type="password" placeholder="Password" value={authPassword} onChange={e => setAuthPassword(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  <button type="submit" className="w-full bg-emerald-600 hover:bg-emerald-500 py-2 rounded-lg text-xs font-semibold">Login User</button>
                </form>
              </div>

              <div className="flex gap-3">
                <button onClick={handleFetchProfile} className="flex-1 bg-slate-800 hover:bg-slate-700 text-slate-200 py-2 rounded-lg text-xs font-semibold">
                  GET /api/v1/users/me (Fetch Profile)
                </button>
              </div>

              {userProfile && (
                <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-xs space-y-1">
                  <span className="text-purple-400 font-semibold">Authenticated Profile:</span>
                  <p className="text-slate-300">Name: {userProfile.fullName} (@{userProfile.username})</p>
                  <p className="text-slate-400">Email: {userProfile.email} | Auth Provider: {userProfile.authProvider}</p>
                </div>
              )}
            </div>
          )}

          {/* TAB 2: CHAINS MANAGEMENT */}
          {activeTab === 'chains' && (
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 space-y-6">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                  <Shield className="w-5 h-5 text-purple-400" /> PMO & Habit Chains Management
                </h2>
                <button onClick={handleFetchChains} className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-xs rounded-lg flex items-center gap-1">
                  <RefreshCw className="w-3 h-3" /> Fetch Chains
                </button>
              </div>

              <form onSubmit={handleCreateChain} className="space-y-3 bg-slate-950 p-4 rounded-xl border border-slate-800">
                <h3 className="text-sm font-semibold text-purple-400">POST /api/v1/chains (PMO Recovery Default)</h3>
                
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <input type="text" placeholder="Title" value={chainTitle} onChange={e => setChainTitle(e.target.value)} className="bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  <select value={chainSubCategory} onChange={e => setChainSubCategory(e.target.value)} className="bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs text-purple-300 font-semibold">
                    <option value="PMO_RECOVERY">PMO_RECOVERY</option>
                    <option value="SMOKING_VAPING">SMOKING_VAPING</option>
                    <option value="DIGITAL_SCROLLING">DIGITAL_SCROLLING</option>
                    <option value="GENERAL_HABIT">GENERAL_HABIT</option>
                  </select>
                </div>

                <textarea placeholder="Description" value={chainDesc} onChange={e => setChainDesc(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs h-16" />

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div>
                    <label className="text-[10px] text-slate-500">Category</label>
                    <select value={chainCategory} onChange={e => setChainCategory(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs">
                      <option value="SPIRITUAL_MORAL">SPIRITUAL_MORAL</option>
                      <option value="LIFESTYLE_PRODUCTIVITY">LIFESTYLE_PRODUCTIVITY</option>
                    </select>
                  </div>
                  <div>
                    <label className="text-[10px] text-slate-500">Privacy Level</label>
                    <select value={chainPrivacy} onChange={e => setChainPrivacy(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs">
                      <option value="LEVEL_0_PRIVATE">LEVEL_0_PRIVATE</option>
                      <option value="LEVEL_1_STREAK_ONLY">LEVEL_1_STREAK_ONLY</option>
                      <option value="LEVEL_2_FULL_COUNSEL">LEVEL_2_FULL_COUNSEL</option>
                    </select>
                  </div>
                  <div>
                    <label className="text-[10px] text-slate-500">Time Wasted (mins)</label>
                    <input type="number" value={chainTime} onChange={e => setChainTime(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  </div>
                </div>

                <input type="text" placeholder="Triggers (comma separated)" value={chainTriggers} onChange={e => setChainTriggers(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                <textarea placeholder="Substitute Action" value={chainSubstitute} onChange={e => setChainSubstitute(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs h-14" />
                <textarea placeholder="Intent Statement (Niyyah)" value={chainIntent} onChange={e => setChainIntent(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs h-14" />

                <button type="submit" className="w-full bg-purple-600 hover:bg-purple-500 py-2.5 rounded-lg text-xs font-semibold flex items-center justify-center gap-1.5">
                  <Plus className="w-4 h-4" /> Create PMO Habit Chain
                </button>
              </form>
            </div>
          )}

          {/* TAB 3: CHECK-IN & TAWBAH PROTOCOL */}
          {activeTab === 'checkin' && (
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 space-y-6">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                  <CheckCircle className="w-5 h-5 text-emerald-400" /> Daily Check-In & Tawbah Protocol
                </h2>
                <button onClick={handleFetchLogs} className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-xs rounded-lg flex items-center gap-1">
                  <RefreshCw className="w-3 h-3" /> Fetch Logs
                </button>
              </div>

              <form onSubmit={handleLogCheckIn} className="space-y-4 bg-slate-950 p-4 rounded-xl border border-slate-800">
                <div className="grid grid-cols-3 gap-3">
                  {[
                    { id: 'CLEAN', label: 'CLEAN', color: 'bg-emerald-600' },
                    { id: 'URGE_RESISTED', label: 'RESISTED URGE', color: 'bg-blue-600' },
                    { id: 'SLIP_UP', label: 'SLIP UP', color: 'bg-rose-600' }
                  ].map(st => (
                    <button
                      key={st.id}
                      type="button"
                      onClick={() => setCheckInStatus(st.id)}
                      className={`py-2 rounded-lg text-xs font-bold transition-all ${
                        checkInStatus === st.id ? `${st.color} text-white ring-2 ring-white/50` : 'bg-slate-900 text-slate-400 hover:bg-slate-800'
                      }`}
                    >
                      {st.label}
                    </button>
                  ))}
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="text-[10px] text-slate-400">Urge Intensity (1-10)</label>
                    <input type="number" min="1" max="10" value={checkInIntensity} onChange={e => setCheckInIntensity(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  </div>
                  <div>
                    <label className="text-[10px] text-slate-400">Trigger Tag</label>
                    <input type="text" value={checkInTrigger} onChange={e => setCheckInTrigger(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  </div>
                </div>

                <textarea placeholder="Reflection Note" value={checkInNote} onChange={e => setCheckInNote(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs h-14" />
                <input type="text" placeholder="Good Deed Done (Hasanat)" value={checkInDeed} onChange={e => setCheckInDeed(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />

                <button type="submit" className="w-full bg-emerald-600 hover:bg-emerald-500 py-2.5 rounded-lg text-xs font-semibold">
                  POST /api/v1/chains/&#123;id&#125;/logs (Submit Check-In)
                </button>
              </form>
            </div>
          )}

          {/* TAB 4: EMERGENCY SOS */}
          {activeTab === 'sos' && (
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 space-y-6">
              <h2 className="text-lg font-bold text-rose-400 flex items-center gap-2">
                <PhoneCall className="w-5 h-5" /> Emergency "Break the Loop" SOS Panic Button
              </h2>

              <div className="bg-rose-950/30 border border-rose-500/30 rounded-xl p-4 text-xs space-y-3">
                <p className="text-rose-300 font-medium">
                  Trigger an immediate 1-tap intervention payload: Physical circuit breaker, Wudu cold water protocol, Ayat al-Kursi shield & 60s urge-surfing box breathing timer.
                </p>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="text-[10px] text-slate-400">Intervention Type</label>
                    <select value={sosType} onChange={e => setSosType(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs">
                      <option value="PHYSICAL_CIRCUIT_BREAKER">PHYSICAL_CIRCUIT_BREAKER</option>
                      <option value="SPIRITUAL">SPIRITUAL</option>
                      <option value="PSYCHOLOGICAL">PSYCHOLOGICAL</option>
                    </select>
                  </div>
                  <div>
                    <label className="text-[10px] text-slate-400">Craving Intensity Before (1-10)</label>
                    <input type="number" min="1" max="10" value={sosCravingBefore} onChange={e => setSosCravingBefore(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  </div>
                </div>

                <button onClick={handleStartSos} className="w-full bg-rose-600 hover:bg-rose-500 text-white font-bold py-3 rounded-xl flex items-center justify-center gap-2 shadow-lg shadow-rose-900/40">
                  <AlertTriangle className="w-5 h-5" /> 1-TAP EMERGENCY SOS BUTTON
                </button>
              </div>

              {activeSosSession && (
                <div className="bg-slate-950 border border-purple-500/40 rounded-xl p-4 text-xs space-y-4">
                  <h3 className="text-sm font-bold text-purple-400">{activeSosSession.title}</h3>
                  <p className="text-slate-300">{activeSosSession.subtitle}</p>

                  <div className="p-3 bg-rose-950/50 border border-rose-500/40 rounded-lg text-rose-300 font-semibold">
                    {activeSosSession.immediatePhysicalStep}
                  </div>

                  <div className="p-3 bg-blue-950/50 border border-blue-500/40 rounded-lg text-blue-300">
                    {activeSosSession.waterProtocolStep}
                  </div>

                  <div className="p-3 bg-emerald-950/50 border border-emerald-500/40 rounded-lg text-emerald-300 italic">
                    {activeSosSession.spiritualShield}
                  </div>

                  <div className="flex justify-between items-center bg-slate-900 p-3 rounded-lg border border-slate-800">
                    <span>Complete Session & Record Craving Drop:</span>
                    <div className="flex gap-2">
                      <input type="number" value={sosCravingAfter} onChange={e => setSosCravingAfter(e.target.value)} placeholder="Craving After" className="w-16 bg-slate-950 border border-slate-800 p-1 text-center rounded text-xs" />
                      <button onClick={handleCompleteSos} className="bg-purple-600 hover:bg-purple-500 px-3 py-1 rounded font-semibold text-xs">Complete</button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* TAB 5: MENTORSHIP & 2-WAY CHAT */}
          {activeTab === 'counsel' && (
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 space-y-6">
              <h2 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                <Heart className="w-5 h-5 text-rose-400" /> Mentorship, Counsel & 2-Way Chat (Suhbah & Nasiha)
              </h2>

              <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-3">
                <h3 className="text-sm font-semibold text-purple-400">1. Generate Encrypted Mentor Invite Code</h3>
                <button onClick={handleGenerateInvite} className="w-full bg-purple-600 hover:bg-purple-500 py-2 rounded-lg text-xs font-semibold">
                  POST /api/v1/chains/&#123;id&#125;/partners/invite
                </button>
                {generatedInviteCode && (
                  <div className="p-3 bg-purple-950/50 border border-purple-500/40 rounded-lg flex items-center justify-between text-xs">
                    <span className="text-purple-300 font-mono font-bold">Invite Code: {generatedInviteCode}</span>
                    <button onClick={() => navigator.clipboard.writeText(generatedInviteCode)} className="text-slate-400 hover:text-white"><Copy className="w-4 h-4" /></button>
                  </div>
                )}
              </div>

              <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-3">
                <h3 className="text-sm font-semibold text-emerald-400">2. Accept Partner Invite Code</h3>
                <input type="text" placeholder="Enter Invite Code (e.g. SUHBAH-A1B2C3)" value={inviteCodeInput} onChange={e => setInviteCodeInput(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs font-mono" />
                <button onClick={handleAcceptInvite} className="w-full bg-emerald-600 hover:bg-emerald-500 py-2 rounded-lg text-xs font-semibold">
                  POST /api/v1/partners/accept
                </button>
              </div>

              <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-3">
                <h3 className="text-sm font-semibold text-blue-400">3. Submit Mentor Counsel Note (Restricted to Accepted Mentor)</h3>
                <textarea value={counselNoteContent} onChange={e => setCounselNoteContent(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs h-16" />
                <button onClick={handleCreateCounselNote} className="w-full bg-blue-600 hover:bg-blue-500 py-2 rounded-lg text-xs font-semibold">
                  POST /api/v1/chains/&#123;id&#125;/counsel-notes
                </button>
              </div>

              {/* 2-WAY CHAT SECTION */}
              <div className="bg-slate-950 p-4 rounded-xl border border-purple-500/40 space-y-3">
                <h3 className="text-sm font-semibold text-purple-300 flex items-center gap-1.5">
                  <MessageSquare className="w-4 h-4 text-purple-400" /> 4. 2-Way Mentorship Chat Thread
                </h3>

                <div>
                  <label className="text-[10px] text-slate-400">Partnership ID</label>
                  <input type="text" placeholder="Partnership UUID" value={partnershipIdInput} onChange={e => setPartnershipIdInput(e.target.value)} className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs font-mono" />
                </div>

                <div className="flex gap-2">
                  <input type="text" placeholder="Type a message..." value={chatMessageContent} onChange={e => setChatMessageContent(e.target.value)} className="flex-1 bg-slate-900 border border-slate-800 rounded-lg p-2 text-xs" />
                  <button onClick={handleSendChatMessage} className="bg-purple-600 hover:bg-purple-500 px-4 py-2 rounded-lg text-xs font-semibold flex items-center gap-1">
                    <Send className="w-3.5 h-3.5" /> Send
                  </button>
                </div>

                <button onClick={handleFetchChatMessages} className="w-full bg-slate-900 hover:bg-slate-800 text-slate-300 py-1.5 rounded-lg text-xs font-medium border border-slate-800 flex items-center justify-center gap-1">
                  <RefreshCw className="w-3 h-3" /> GET /api/v1/partnerships/&#123;partnershipId&#125;/messages
                </button>

                {chatMessages.length > 0 && (
                  <div className="space-y-2 max-h-48 overflow-y-auto p-2 bg-slate-900 rounded-lg border border-slate-800 text-xs">
                    {chatMessages.map(m => (
                      <div key={m.id} className="p-2 bg-slate-950 rounded border border-slate-800 space-y-0.5">
                        <div className="flex justify-between text-[10px] text-purple-400">
                          <span className="font-bold">{m.senderFullName} (@{m.senderUsername})</span>
                          <span className="text-slate-500">{new Date(m.createdAt).toLocaleTimeString()}</span>
                        </div>
                        <p className="text-slate-200">{m.messageContent}</p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}

          {/* TAB 6: ANALYTICS & MILESTONES */}
          {activeTab === 'analytics' && (
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 space-y-6">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                  <Award className="w-5 h-5 text-amber-400" /> Analytics, Barakah & Milestones
                </h2>
                <div className="flex gap-2">
                  <button onClick={handleFetchAnalytics} className="px-3 py-1.5 bg-purple-600 hover:bg-purple-500 text-xs font-semibold rounded-lg">Fetch Analytics</button>
                  <button onClick={handleFetchMilestones} className="px-3 py-1.5 bg-amber-600 hover:bg-amber-500 text-xs font-semibold rounded-lg">Fetch Badges</button>
                </div>
              </div>

              {analytics && (
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
                  <div className="bg-slate-950 p-3 rounded-xl border border-slate-800">
                    <span className="text-[10px] text-slate-400">Clean Percentage</span>
                    <p className="text-xl font-bold text-emerald-400">{analytics.cleanPercentage}%</p>
                  </div>
                  <div className="bg-slate-950 p-3 rounded-xl border border-slate-800">
                    <span className="text-[10px] text-slate-400">Current Streak</span>
                    <p className="text-xl font-bold text-amber-400">{analytics.currentStreakDays} Days</p>
                  </div>
                  <div className="bg-slate-950 p-3 rounded-xl border border-slate-800">
                    <span className="text-[10px] text-slate-400">Money Saved</span>
                    <p className="text-xl font-bold text-purple-400">${analytics.moneySaved}</p>
                  </div>
                  <div className="bg-slate-950 p-3 rounded-xl border border-slate-800">
                    <span className="text-[10px] text-slate-400">Time Saved</span>
                    <p className="text-xl font-bold text-blue-400">{analytics.timeSavedHours} Hours</p>
                  </div>
                </div>
              )}

              {milestones.length > 0 && (
                <div className="space-y-3">
                  <h3 className="text-xs font-bold text-amber-400">Earned Neuroplasticity & Nafs Badges:</h3>
                  <div className="space-y-2">
                    {milestones.map(m => (
                      <div key={m.badgeId} className="bg-slate-950 p-3 rounded-xl border border-amber-500/30 flex items-center gap-3">
                        <Award className="w-6 h-6 text-amber-400" />
                        <div>
                          <p className="text-xs font-bold text-slate-200">{m.title}</p>
                          <p className="text-[11px] text-slate-400">{m.description}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}

        </main>

        {/* Right Column: Live Console & Inspection */}
        <aside className="lg:col-span-5 space-y-4">
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-4 sticky top-6">
            <div className="flex items-center justify-between pb-3 mb-3 border-b border-slate-800">
              <div className="flex items-center gap-2">
                <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></div>
                <span className="text-xs font-bold text-slate-300">Live API Inspector Console</span>
              </div>
              {apiResponse && (
                <span className={`text-[10px] font-mono font-bold px-2 py-0.5 rounded ${
                  apiResponse.status < 300 ? 'bg-emerald-950 text-emerald-400 border border-emerald-500/30' : 'bg-rose-950 text-rose-400 border border-rose-500/30'
                }`}>
                  HTTP {apiResponse.status}
                </span>
              )}
            </div>

            {loading ? (
              <div className="py-12 text-center text-xs text-purple-400 flex items-center justify-center gap-2">
                <RefreshCw className="w-4 h-4 animate-spin" /> Executing Spring Boot API Request...
              </div>
            ) : apiResponse ? (
              <div className="space-y-3">
                {apiResponse.requestId && (
                  <div className="text-[10px] font-mono text-slate-500 flex justify-between">
                    <span>X-Request-ID:</span>
                    <span>{apiResponse.requestId}</span>
                  </div>
                )}
                
                {/* Visual Guidance Banner if present */}
                {apiResponse.data?.data?.postSlipGuidance && (
                  <div className="p-3 bg-purple-950/60 border border-purple-500/50 rounded-xl space-y-2 text-xs">
                    <span className="font-bold text-purple-300 flex items-center gap-1">
                      <Sparkles className="w-3.5 h-3.5" /> Post-Slip Tawbah Guidance Protocol Received:
                    </span>
                    <p className="font-semibold text-slate-200">{apiResponse.data.data.postSlipGuidance.title}</p>
                    <p className="text-[11px] text-emerald-400 italic">{apiResponse.data.data.postSlipGuidance.spiritualRemind}</p>
                    <div className="p-2 bg-slate-900 rounded border border-slate-800 text-[11px] text-rose-300 font-semibold">
                      {apiResponse.data.data.postSlipGuidance.chaserEffectWarning}
                    </div>
                  </div>
                )}

                <div className="relative">
                  <pre className="bg-slate-950 p-3 rounded-lg border border-slate-800 text-[11px] font-mono text-slate-300 overflow-x-auto max-h-[500px]">
                    {JSON.stringify(apiResponse.data, null, 2)}
                  </pre>
                  <button 
                    onClick={() => navigator.clipboard.writeText(JSON.stringify(apiResponse.data, null, 2))}
                    className="absolute top-2 right-2 p-1 bg-slate-800 hover:bg-slate-700 rounded text-slate-400 hover:text-white text-[10px] flex items-center gap-1"
                  >
                    <Copy className="w-3 h-3" /> Copy
                  </button>
                </div>
              </div>
            ) : (
              <div className="py-12 text-center text-xs text-slate-500 italic">
                Select any tab and click an API action to inspect raw JSON payloads, request IDs, and live backend responses.
              </div>
            )}
          </div>
        </aside>

      </div>
    </div>
  );
}
