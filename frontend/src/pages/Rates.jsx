import { useEffect, useState } from 'react';
import api from '../api/client';
import { formatCurrency, formatDate } from '../utils/format';

const METALS = ['GOLD', 'SILVER', 'PLATINUM'];
const PURITIES = {
  GOLD: ['22K', '24K', '18K'],
  SILVER: ['925', '999'],
  PLATINUM: ['950'],
};

export default function Rates() {
  const [rates, setRates] = useState([]);
  const [form, setForm] = useState({ metalType: 'GOLD', purity: '22K', ratePerGram: '' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadRates = () => api.get('/rates').then(({ data }) => setRates(data));

  useEffect(() => {
    loadRates();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');
    try {
      await api.post('/rates', {
        ...form,
        ratePerGram: Number(form.ratePerGram),
      });
      setForm((prev) => ({ ...prev, ratePerGram: '' }));
      setMessage('Rate saved. New invoices will use this price.');
      loadRates();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save rate');
    }
  };

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h2>Daily Rates</h2>
          <p>Insert a new rate row — history is preserved for past invoices</p>
        </div>
      </header>

      <div className="billing-grid">
        <form className="panel" onSubmit={handleSubmit}>
          <h3>Update rate (manual)</h3>
          <div className="form-row">
            <label>
              Metal
              <select
                value={form.metalType}
                onChange={(e) => setForm({ metalType: e.target.value, purity: PURITIES[e.target.value][0], ratePerGram: '' })}
              >
                {METALS.map((m) => <option key={m} value={m}>{m}</option>)}
              </select>
            </label>
            <label>
              Purity
              <select
                value={form.purity}
                onChange={(e) => setForm((prev) => ({ ...prev, purity: e.target.value }))}
              >
                {PURITIES[form.metalType].map((p) => <option key={p} value={p}>{p}</option>)}
              </select>
            </label>
            <label>
              Rate per gram (₹)
              <input
                required
                type="number"
                min="0.0001"
                step="0.0001"
                value={form.ratePerGram}
                onChange={(e) => setForm((prev) => ({ ...prev, ratePerGram: e.target.value }))}
              />
            </label>
          </div>
          <button type="submit" className="primary-btn">Save today&apos;s rate</button>
          {message && <div className="alert success">{message}</div>}
          {error && <div className="alert error">{error}</div>}
          <p className="hint">
            Recommended: managers enter rates manually each morning. Optional API auto-fetch can be enabled in backend config.
          </p>
        </form>

        <section className="panel">
          <h3>Current active rates</h3>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Metal</th>
                  <th>Purity</th>
                  <th>Rate/g</th>
                  <th>Updated</th>
                  <th>Source</th>
                </tr>
              </thead>
              <tbody>
                {rates.map((rate) => (
                  <tr key={rate.id}>
                    <td>{rate.metalType}</td>
                    <td>{rate.purity}</td>
                    <td>{formatCurrency(rate.ratePerGram)}</td>
                    <td>{formatDate(rate.updatedAt)}</td>
                    <td>{rate.source}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </div>
  );
}
