import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import { formatCurrency, formatDate } from '../utils/format';

export default function Dashboard() {
  const [rates, setRates] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([api.get('/rates'), api.get('/invoices')])
      .then(([ratesRes, invoicesRes]) => {
        setRates(ratesRes.data);
        setInvoices(invoicesRes.data.slice(0, 8));
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page-loading">Loading dashboard…</div>;

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h2>Dashboard</h2>
          <p>Today&apos;s metal rates and recent invoices</p>
        </div>
        <Link to="/billing" className="primary-btn">+ New Invoice</Link>
      </header>

      <section className="card-grid">
        {rates.map((rate) => (
          <article key={rate.id} className="stat-card">
            <span className="stat-label">{rate.metalType} · {rate.purity}</span>
            <strong>{formatCurrency(rate.ratePerGram)}</strong>
            <small>per gram · {formatDate(rate.updatedAt)}</small>
          </article>
        ))}
      </section>

      <section className="panel">
        <div className="panel-header">
          <h3>Recent Invoices</h3>
        </div>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Invoice #</th>
                <th>Customer</th>
                <th>Date</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              {invoices.length === 0 ? (
                <tr>
                  <td colSpan="4" className="empty">No invoices yet</td>
                </tr>
              ) : (
                invoices.map((inv) => (
                  <tr key={inv.id}>
                    <td>#{inv.id}</td>
                    <td>{inv.customer?.name || 'Walk-in'}</td>
                    <td>{formatDate(inv.invoiceDate)}</td>
                    <td>{formatCurrency(inv.totalAmount)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
