import http from 'k6/http';
import { check } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 10 },
        { duration: '30s', target: 50 },
        { duration: '30s', target: 100 },
        { duration: '10s', target: 0 },
    ],
};

export default function () {
    const payload = JSON.stringify({
        originalUrl: 'https://www.google.com',
    });

    const res = http.post('http://localhost:9876/api/urls', payload,
        {
            headers: { 'Content-Type': 'application/json' },
        });

    check(res, {
        'status is 201': (r) => r.status === 201,
    });
}