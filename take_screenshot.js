const { chromium } = require('playwright');
const path = require('path');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  await page.setViewportSize({ width: 1280, height: 900 });

  try {
    // Step 1: Navigate to cv/upload
    await page.goto('http://localhost:3000/cv/upload', { waitUntil: 'networkidle', timeout: 15000 });
    const url1 = page.url();
    console.log('URL after navigate to /cv/upload:', url1);

    // Step 2: Take initial screenshot
    await page.screenshot({ path: 'D:/study-dev/WebTuyenDung/cv_screenshot_step1.png', fullPage: true });
    console.log('Step 1 screenshot saved');

    // Step 3: Check if on login page
    const isLoginPage = url1.includes('login') || (await page.$('input[type="password"]')) !== null;

    if (isLoginPage) {
      console.log('On login page, attempting login with candidate1/123456...');

      // Try to find username field
      const usernameSelectors = [
        'input[name="username"]',
        'input[name="email"]',
        'input[type="text"]',
        'input[placeholder*="user" i]',
        'input[placeholder*="email" i]',
        'input[placeholder*="tên" i]',
        'input[id*="user" i]',
        'input[id*="email" i]',
      ];

      let userInput = null;
      for (const sel of usernameSelectors) {
        userInput = await page.$(sel);
        if (userInput) {
          console.log('Found username field with selector:', sel);
          break;
        }
      }

      const passInput = await page.$('input[type="password"]');

      if (userInput && passInput) {
        await userInput.fill('candidate1');
        await passInput.fill('123456');

        // Take screenshot after filling
        await page.screenshot({ path: 'D:/study-dev/WebTuyenDung/cv_login_filled.png', fullPage: true });
        console.log('Login form filled screenshot saved');

        // Find and click login button
        const loginBtnSelectors = [
          'button[type="submit"]',
          'button:has-text("Login")',
          'button:has-text("Đăng nhập")',
          'input[type="submit"]',
        ];

        let loginBtn = null;
        for (const sel of loginBtnSelectors) {
          loginBtn = await page.$(sel);
          if (loginBtn) {
            console.log('Found login button with selector:', sel);
            break;
          }
        }

        if (loginBtn) {
          await loginBtn.click();
          await page.waitForNavigation({ waitUntil: 'networkidle', timeout: 10000 }).catch(() => {});
        }
      } else {
        console.log('Could not find username or password fields');
        const inputs = await page.$$eval('input', els => els.map(e => ({ type: e.type, name: e.name, placeholder: e.placeholder, id: e.id })));
        console.log('All inputs on page:', JSON.stringify(inputs));
      }

      const url2 = page.url();
      console.log('URL after login attempt:', url2);

      // Navigate back to cv/upload
      await page.goto('http://localhost:3000/cv/upload', { waitUntil: 'networkidle', timeout: 15000 });
      console.log('URL after re-navigate to /cv/upload:', page.url());
    }

    // Step 5: Full page screenshot
    await page.screenshot({ path: 'D:/study-dev/WebTuyenDung/cv_screenshot.png', fullPage: true });
    console.log('Full page screenshot saved: cv_screenshot.png');

    // Step 6: Viewport screenshot for CV list area
    await page.screenshot({ path: 'D:/study-dev/WebTuyenDung/cv_screenshot2.png', fullPage: false });
    console.log('Viewport screenshot saved: cv_screenshot2.png');

    // Get page info
    const title = await page.title();
    const h1 = await page.$eval('h1', el => el.textContent).catch(() => 'none');
    const h2 = await page.$$eval('h2', els => els.map(e => e.textContent)).catch(() => []);
    const bodyText = await page.$eval('body', el => el.innerText.slice(0, 1000)).catch(() => '');
    console.log('Title:', title);
    console.log('H1:', h1);
    console.log('H2s:', JSON.stringify(h2));
    console.log('Body text preview:\n', bodyText);

    // Check for tables/cards
    const tables = await page.$$('table');
    const cards = await page.$$('.card, [class*="card"]');
    console.log('Tables found:', tables.length);
    console.log('Cards found:', cards.length);

  } catch (err) {
    console.error('Error:', err.message);
    await page.screenshot({ path: 'D:/study-dev/WebTuyenDung/cv_error_screenshot.png', fullPage: true });
    console.log('Error screenshot saved');
  }

  await browser.close();
})();
