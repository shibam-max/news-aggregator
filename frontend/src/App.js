import React, { useState, useEffect } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container, Row, Col, Form, Button, Card, Alert, Spinner, Pagination } from 'react-bootstrap';

function App() {
  const [searchData, setSearchData] = useState({
    keyword: '',
    page: 1,
    pageSize: 10,
    city: '',
    offlineMode: false
  });
  
  const [newsResponse, setNewsResponse] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setSearchData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const searchNews = async (pageNum = 1) => {
    setLoading(true);
    setError(null);
    
    try {
      const params = {
        ...searchData,
        page: pageNum
      };
      
      const response = await axios.get('/api/v1/news/search', { params });
      setNewsResponse(response.data);
    } catch (err) {
      setError('Failed to fetch news. Please try again.');
      console.error('Error fetching news:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (searchData.keyword.trim()) {
      searchNews(1);
    }
  };

  const handlePageChange = (pageNum) => {
    setSearchData(prev => ({ ...prev, page: pageNum }));
    searchNews(pageNum);
  };

  return (
    <Container className="py-4">
      <Row>
        <Col>
          <h1 className="text-center mb-4">📰 News Aggregator</h1>
          <p className="text-center text-muted">Search news from Guardian and NY Times</p>
        </Col>
      </Row>

      <Row className="justify-content-center">
        <Col md={8}>
          <Card className="mb-4">
            <Card.Body>
              <Form onSubmit={handleSubmit}>
                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Search Keyword *</Form.Label>
                      <Form.Control
                        type="text"
                        name="keyword"
                        value={searchData.keyword}
                        onChange={handleInputChange}
                        placeholder="Enter search keyword (e.g., apple)"
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group className="mb-3">
                      <Form.Label>Page Size</Form.Label>
                      <Form.Select name="pageSize" value={searchData.pageSize} onChange={handleInputChange}>
                        <option value={5}>5</option>
                        <option value={10}>10</option>
                        <option value={20}>20</option>
                      </Form.Select>
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group className="mb-3">
                      <Form.Label>City (Optional)</Form.Label>
                      <Form.Control
                        type="text"
                        name="city"
                        value={searchData.city}
                        onChange={handleInputChange}
                        placeholder="City filter"
                      />
                    </Form.Group>
                  </Col>
                </Row>
                
                <Row>
                  <Col md={6}>
                    <Form.Check
                      type="checkbox"
                      name="offlineMode"
                      label="Offline Mode"
                      checked={searchData.offlineMode}
                      onChange={handleInputChange}
                    />
                  </Col>
                  <Col md={6} className="text-end">
                    <Button type="submit" variant="primary" disabled={loading}>
                      {loading ? <Spinner size="sm" /> : 'Search News'}
                    </Button>
                  </Col>
                </Row>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {error && (
        <Row>
          <Col>
            <Alert variant="danger">{error}</Alert>
          </Col>
        </Row>
      )}

      {newsResponse && (
        <>
          <Row>
            <Col>
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h4>Search Results</h4>
                <small className="text-muted">
                  Execution time: {newsResponse.executionTimeMs}ms
                  {newsResponse.fromCache && ' (cached)'}
                  {newsResponse.offlineMode && ' (offline)'}
                </small>
              </div>
              
              <div className="mb-3">
                <strong>Keyword:</strong> {newsResponse.searchKeyword} | 
                <strong> Total Results:</strong> {newsResponse.totalResults} | 
                <strong> Page:</strong> {newsResponse.currentPage} of {newsResponse.totalPages}
                {newsResponse.city && <span> | <strong>City:</strong> {newsResponse.city}</span>}
              </div>
            </Col>
          </Row>

          <Row>
            {newsResponse.articles.map((article, index) => (
              <Col md={6} lg={4} key={article.id} className="mb-4">
                <Card className="h-100">
                  {article.imageUrl && (
                    <Card.Img variant="top" src={article.imageUrl} style={{height: '200px', objectFit: 'cover'}} />
                  )}
                  <Card.Body className="d-flex flex-column">
                    <Card.Title className="h6">{article.title}</Card.Title>
                    <Card.Text className="flex-grow-1 small text-muted">
                      {article.description}
                    </Card.Text>
                    <div className="mt-auto">
                      <small className="text-muted d-block">
                        <strong>{article.source}</strong>
                        {article.author && ` • ${article.author}`}
                        {article.section && ` • ${article.section}`}
                      </small>
                      <small className="text-muted d-block">
                        {new Date(article.publishedAt).toLocaleDateString()}
                      </small>
                      <Button 
                        variant="outline-primary" 
                        size="sm" 
                        href={article.url} 
                        target="_blank" 
                        className="mt-2"
                      >
                        Read Full Article
                      </Button>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>

          {newsResponse.totalPages > 1 && (
            <Row>
              <Col className="d-flex justify-content-center">
                <Pagination>
                  {newsResponse.previousPage && (
                    <Pagination.Prev onClick={() => handlePageChange(newsResponse.previousPage)} />
                  )}
                  
                  {Array.from({ length: Math.min(5, newsResponse.totalPages) }, (_, i) => {
                    const pageNum = Math.max(1, newsResponse.currentPage - 2) + i;
                    if (pageNum <= newsResponse.totalPages) {
                      return (
                        <Pagination.Item
                          key={pageNum}
                          active={pageNum === newsResponse.currentPage}
                          onClick={() => handlePageChange(pageNum)}
                        >
                          {pageNum}
                        </Pagination.Item>
                      );
                    }
                    return null;
                  })}
                  
                  {newsResponse.nextPage && (
                    <Pagination.Next onClick={() => handlePageChange(newsResponse.nextPage)} />
                  )}
                </Pagination>
              </Col>
            </Row>
          )}
        </>
      )}
    </Container>
  );
}

export default App;