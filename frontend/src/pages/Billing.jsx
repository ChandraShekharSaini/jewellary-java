import { useEffect, useState } from 'react';
import api from '../api/client';
import { formatCurrency } from '../utils/format';

const emptyLine = { productId: '', weightGrams: '' };

export default function Billing() {
  const [products, setProducts] = useState([]);
  const [lines, setLines] = useState([{ ...emptyLine }]);
  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [invoice, setInvoice] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api.get('/products').then(({ data }) => setProducts(data));
  }, []);

  const updateLine = (index, field, value) => {
    setLines((prev) => prev.map((line, i) => (i === index ? { ...line, [field]: value } : line)));
  };

  const addLine = () => setLines((prev) => [...prev, { ...emptyLine }]);

  const removeLine = (index) => {
    setLines((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    setInvoice(null);
    try {
      const payload = {
        customerName: customerName || undefined,
        customerPhone: customerPhone || undefined,
        items: lines.map((line) => ({
          productId: Number(line.productId),
          weightGrams: Number(line.weightGrams),
        })),
      };
      const { data } = await api.post('/invoices', payload);
      setInvoice(data);
      setLines([{ ...emptyLine }]);
      setCustomerName('');
      setCustomerPhone('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create invoice');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page billing-page">
      <header className="page-header">
        <div>
          <h2>New Invoice</h2>
          <p>Weigh items and generate a locked-price receipt</p>
        </div>
      </header>

      <div className="billing-grid">
        <form className="panel" onSubmit={handleSubmit}>
          <h3>Customer</h3>
          <div className="form-row">
            <label>
              Name
              <input value={customerName} onChange={(e) => setCustomerName(e.target.value)} placeholder="Optional" />
            </label>
            <label>
              Phone
              <input value={customerPhone} onChange={(e) => setCustomerPhone(e.target.value)} placeholder="Optional" />
            </label>
          </div>

          <h3>Items</h3>
          {lines.map((line, index) => (
            <div className="line-row" key={index}>
              <label>
                Product
                <select
                  required
                  value={line.productId}
                  onChange={(e) => updateLine(index, 'productId', e.target.value)}
                >
                  <option value="">Select product</option>
                  {products.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.name} ({p.purity})
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Weight (g)
                <input
                  required
                  type="number"
                  min="0.0001"
                  step="0.0001"
                  value={line.weightGrams}
                  onChange={(e) => updateLine(index, 'weightGrams', e.target.value)}
                />
              </label>
              {lines.length > 1 && (
                <button type="button" className="ghost-btn" onClick={() => removeLine(index)}>
                  Remove
                </button>
              )}
            </div>
          ))}

          <div className="form-actions">
            <button type="button" className="ghost-btn" onClick={addLine}>+ Add item</button>
            <button type="submit" className="primary-btn" disabled={loading}>
              {loading ? 'Generating…' : 'Generate Invoice'}
            </button>
          </div>
          {error && <div className="alert error">{error}</div>}
        </form>

        {invoice && (
          <section className="panel receipt">
            <div className="receipt-header">
              <h3>Invoice #{invoice.id}</h3>
              <button type="button" className="ghost-btn" onClick={() => window.print()}>
                Print
              </button>
            </div>
            <p className="receipt-meta">
              {invoice.customer?.name || 'Walk-in customer'}
              {invoice.customer?.phone ? ` · ${invoice.customer.phone}` : ''}
            </p>
            <table>
              <thead>
                <tr>
                  <th>Item</th>
                  <th>Weight</th>
                  <th>Rate/g</th>
                  <th>Total</th>
                </tr>
              </thead>
              <tbody>
                {invoice.items.map((item) => (
                  <tr key={item.id}>
                    <td>{item.productName}</td>
                    <td>{item.weightGrams} g</td>
                    <td>{formatCurrency(item.rateApplied)}</td>
                    <td>{formatCurrency(item.lineTotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="receipt-totals">
              <div><span>Subtotal</span><strong>{formatCurrency(invoice.subtotal)}</strong></div>
              <div><span>GST ({invoice.gstPercent}%)</span><strong>{formatCurrency(invoice.taxAmount)}</strong></div>
              <div className="grand-total"><span>Total</span><strong>{formatCurrency(invoice.totalAmount)}</strong></div>
            </div>
          </section>
        )}
      </div>
    </div>
  );
}
