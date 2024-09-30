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
    @Environment(\.colorScheme) var colorScheme
    
    let darkStatusbarColor = Color(red: 0.2, green: 0.2, blue: 0.2)
    let lightStatusbarColor = Color(red: 0.5, green: 0.5, blue: 0.5)
    

    var body: some View {
        ZStack{
            colorScheme == .dark ? darkStatusbarColor.edgesIgnoringSafeArea(.top) : lightStatusbarColor.edgesIgnoringSafeArea(.top)
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
}
