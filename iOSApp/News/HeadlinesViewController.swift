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
    
    private var isLoadingNextPage = false
    
    @IBOutlet private weak var headlinesTableView: UITableView!
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        get { return .lightContent }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureStatusBar()
        configureRefreshControl()
        configureViews()
        
        headlinesPresenter.loadHeadlineAsync(reload: true)
    }
    
    private func configureStatusBar() {
        let effectView = UIVisualEffectView(effect: UIBlurEffect(style: .dark))
        effectView.frame = UIApplication.shared.statusBarFrame
        view.addSubview(effectView)
    }
    
    private func configureRefreshControl() {
        let statusBarFrame = UIApplication.shared.statusBarFrame
        refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(handleRefresh), for: .valueChanged)
        refreshControl.bounds = refreshControl.bounds.offsetBy(dx: 0, dy: 4 + statusBarFrame.height)
    }
    
    private func configureViews() {
        headlinesTableView.contentInsetAdjustmentBehavior = .never
        headlinesTableView.tableFooterView = UIView(frame: .zero)
        headlinesTableView.addSubview(refreshControl)
        headlinesTableView.dataSource = self
        headlinesTableView.delegate = self
        
        headlinesPresenter.view = self
    }
    
    @objc private func handleRefresh() {
        headlinesPresenter.loadHeadlineAsync(reload: true)
    }

}

extension HeadlinesViewController: UITableViewDelegate {
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let lastVisibleRow = headlinesTableView.indexPathsForVisibleRows?.last?.row
        if lastVisibleRow == articles.count - 1 && !isLoadingNextPage {
            headlinesPresenter.loadHeadlineAsync(reload: false)
        }
    }
}

extension HeadlinesViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return isLoadingNextPage ? 2 : 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return articles.count
        } else {
            return 1
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let section = indexPath.section
        if section == 1 {
            return tableView.dequeueReusableCell(withIdentifier: "Loading Progress")!
        } else {
            let row = indexPath.row
            var cellID: String
            if row == 0 {
                cellID = "Enlarged Headline"
            } else {
                cellID = "Normal Headline"
            }
            let cell = tableView.dequeueReusableCell(withIdentifier: cellID) as! HeadlinesTableViewCell
            cell.article = articles[row]
            return cell
        }
    }
}

extension HeadlinesViewController: HeadlinesView {
    
    func onLoad() {
        print("Loading...")
        if headlinesPresenter.currentPage == 1 {
            refreshControl.beginRefreshing()
        } else {
            isLoadingNextPage = true
            headlinesTableView.reloadData()
        }
    }
    
    func onResponse(response: ArticleListResponse) {
        headlinesTableView.reloadData()
        if let articles = response.articles {
            if headlinesPresenter.currentPage == 1 {
                refreshControl.endRefreshing()
                self.articles = articles
            } else {
                isLoadingNextPage = false
                self.articles += articles
            }
        }
        headlinesTableView.reloadData()
    }
    
    func onError(e: KotlinException) {
        e.printStackTrace()
        refreshControl.endRefreshing()
    }
}
