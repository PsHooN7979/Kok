//
//  ViewController.swift
//  webView
//
//  Created by 이상현 on 10/2/24.
//

import UIKit
import WebKit

class WebViewController: UIViewController, WKNavigationDelegate {
    var webView: WKWebView!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let config = WKWebViewConfiguration()
        config.allowsInlineMediaPlayback = true
        
        webView = WKWebView(frame: .zero, configuration: config)
        webView.navigationDelegate = self
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.scrollView.bounces = false
        
        
        //User Agent 수정
//        webView.customUserAgent = "IosApp/1.0"

        self.view.backgroundColor = .white
        self.view.addSubview(webView)
        
        NSLayoutConstraint.activate([
            webView.topAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.topAnchor),
            webView.bottomAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.bottomAnchor),
            webView.leadingAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.leadingAnchor),
            webView.trailingAnchor.constraint(equalTo: self.view.safeAreaLayoutGuide.trailingAnchor)
        ])
        
        loadWebView()
        updateBackgroundColor()
    }
    
    func loadWebView(){
        if let url = URL(string: "https://www.naver.com"){
            let request = URLRequest(url: url)
            webView.load(request)
        }
    }
    
    //화면 모드에 따라 상태바 색 변경
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        if previousTraitCollection != nil {
            updateBackgroundColor()
        }
    }
    
    private func updateBackgroundColor() {
        if traitCollection.userInterfaceStyle == .dark {
            self.view.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 1.0)    //0 ~ 1 사이 값 0.1당 25
        } else {
            self.view.backgroundColor = UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 1.0)
        }
    }
    
    /*
    토큰과 관련된 코드
    
    if let token = TokenModel.shared.getToken(){
        loadWebView(with: token)
    }
     
     
    func loadWebView(wth token: String){
        if let url = URL(string: "https://apple.com/kr"){
            var request = URLRequest(url: url)
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
            webView.load(request)
        }
    }
     */
}

extension AppDelegate {
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        // 회전 비활성화 (세로 모드 고정)
        return .portrait
    }
}
