import http from 'k6/http';
import {check} from 'k6';

const BASE_URL = 'http://localhost:9876';

export const options = {
    vus: 100,
    duration: '30s',
};

export function setup() {
    const payload = JSON.stringify({
        originalUrl: 'https://www.google.com',
    });
    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(`${BASE_URL}/api/urls`, payload, { headers });

    if (res.status !== 201) {
        throw new Error(`Setup failed: ${res.status} ${res.body}`);
    }
    const shortKey = res.json('shortKey');
    console.log(`Setup: created shortKey = ${shortKey}`);
    return { shortKey };
}

export default function (data) {
    const res = http.get(`${BASE_URL}/${data.shortKey}`, { redirects: 0 });
    check(res, {
        'status is 302': (r) => r.status === 302,
    });
}