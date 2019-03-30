//
//  ViewController.swift
//  News
//
//  Created by Visutr Boonnateephisit on 30/3/19.
//  Copyright © 2019 Visutr Boonnateephisit. All rights reserved.
//

import UIKit
import SharedCode

class HeadlinesViewController: UIViewController {
    
    private var articles = [Article]()
    private lazy var headlinesPresenter = { PresenterFactory().createHeadlinesPresenter() }()
    
    private var refreshControl: UIRefreshControl!
    
    @IBOutlet private weak var headlinesTableView: UITableView!
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        get { return .lightContent }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureStatusBar()
        configureRefreshControl()
        configureViews()
        
        headlinesPresenter.loadHeadlineAsync()
    }
    
    private func configureStatusBar() {
        let effectView = UIVisualEffectView(effect: UIBlurEffect(style: .dark))
        effectView.frame = UIApplication.shared.statusBarFrame
        view.addSubview(effectView)
    }
    
    private func configureRefreshControl() {
        refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(handleRefresh), for: .valueChanged)
    }
    
    private func configureViews() {
        headlinesTableView.contentInsetAdjustmentBehavior = .never
        headlinesTableView.tableFooterView = UIView(frame: .zero)
        headlinesTableView.dataSource = self
        headlinesTableView.addSubview(refreshControl)
        
        headlinesPresenter.view = self
    }
    
    @objc private func handleRefresh() {
        headlinesPresenter.loadHeadlineAsync()
    }
}

extension HeadlinesViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return articles.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let row = indexPath.row
        var cellID: String
        if row == 0 {
            cellID = "Enlarged Headline"
        } else {
            cellID = "Normal Headline"
        }
        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellID)
            as? HeadlinesTableViewCell else {
                fatalError("Invalid cell identifier")
        }
        cell.article = articles[row]
        return cell
    }
}

extension HeadlinesViewController: HeadlinesView {
    
    func onLoad() {
        print("Loading...")
        refreshControl.beginRefreshing()
    }
    
    func onResponse(response: ArticleListResponse) {
        if response.articles != nil {
            articles = response.articles!
        }
        headlinesTableView.reloadData()
        refreshControl.endRefreshing()
    }
    
    func onError(e: KotlinException) {
        e.printStackTrace()
        refreshControl.endRefreshing()
    }
}
