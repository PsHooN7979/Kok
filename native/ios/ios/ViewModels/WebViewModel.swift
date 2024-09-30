//
//  WebViewModel.swift
//  ios
//
//  Created by 이상현 on 9/28/24.
//

import Foundation
import Combine

class WebViewModel: ObservableObject {
    @Published var webPage: WebPage?

    init(urlString: String) {
        if let url = URL(string: urlString) {
            self.webPage = WebPage(url: url)
        }
    }
}
