import React, { useState, useCallback } from 'react';
import PropTypes from 'prop-types';
import Input from '../atoms/Input';
import Button from '../atoms/Button';

const SearchForm = ({ onSearch, loading = false, initialValues = {} }) => {
  const [formData, setFormData] = useState({
    keyword: initialValues.keyword || '',
    city: initialValues.city || '',
    pageSize: initialValues.pageSize || 10,
    offlineMode: initialValues.offlineMode || false
  });
  
  const [errors, setErrors] = useState({});
  
  const validateForm = useCallback(() => {
    const newErrors = {};
    
    if (!formData.keyword.trim()) {
      newErrors.keyword = 'Search keyword is required';
    } else if (formData.keyword.trim().length < 2) {
      newErrors.keyword = 'Search keyword must be at least 2 characters';
    }
    
    if (formData.pageSize < 1 || formData.pageSize > 50) {
      newErrors.pageSize = 'Page size must be between 1 and 50';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [formData]);
  
  const handleInputChange = useCallback((field) => (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: ''
      }));
    }
  }, [errors]);
  
  const handleSubmit = useCallback((e) => {
    e.preventDefault();
    
    if (validateForm()) {
      onSearch({
        ...formData,
        page: 1 // Reset to first page on new search
      });
    }
  }, [formData, validateForm, onSearch]);
  
  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-md space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          id="keyword"
          name="keyword"
          label="Search Keyword"
          placeholder="Enter search term (e.g., technology, sports)"
          value={formData.keyword}
          onChange={handleInputChange('keyword')}
          error={errors.keyword}
          required
          disabled={loading}
        />
        
        <Input
          id="city"
          name="city"
          label="City (Optional)"
          placeholder="Enter city name"
          value={formData.city}
          onChange={handleInputChange('city')}
          error={errors.city}
          disabled={loading}
        />
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          id="pageSize"
          name="pageSize"
          type="number"
          label="Results per page"
          placeholder="10"
          value={formData.pageSize}
          onChange={handleInputChange('pageSize')}
          error={errors.pageSize}
          min="1"
          max="50"
          disabled={loading}
        />
        
        <div className="flex items-center space-x-2 pt-6">
          <input
            id="offlineMode"
            name="offlineMode"
            type="checkbox"
            checked={formData.offlineMode}
            onChange={handleInputChange('offlineMode')}
            disabled={loading}
            className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
          />
          <label htmlFor="offlineMode" className="text-sm font-medium text-gray-700">
            Offline Mode
          </label>
        </div>
      </div>
      
      <div className="flex justify-center pt-4">
        <Button
          type="submit"
          variant="primary"
          size="large"
          loading={loading}
          disabled={loading}
          className="w-full md:w-auto px-8"
        >
          {loading ? 'Searching...' : 'Search News'}
        </Button>
      </div>
    </form>
  );
};

SearchForm.propTypes = {
  onSearch: PropTypes.func.isRequired,
  loading: PropTypes.bool,
  initialValues: PropTypes.object
};

export default SearchForm;