//
//  WebViewScreen.swift
//  ios
//
//  Created by 이상현 on 9/28/24.
//

import SwiftUI
import WebKit

struct WebViewScreen: View {
    @StateObject private var viewModel = WebViewModel(urlString: "https://www.apple.com")

    var body: some View {
        VStack {
            if let webPage = viewModel.webPage {
                WebView(url: webPage.url)
                    .edgesIgnoringSafeArea(.bottom) // 하단 안전 영역 무시
            } else {
                Text("Invalid URL")
            }
        }
    }
}
