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
    
    private var isLastPage = false {
        didSet { headlinesTableView.reloadData() }
    }
    
    private var isLoading = false
    
    @IBOutlet private weak var headlinesTableView: UITableView!
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        get { return .lightContent }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureStatusBar()
        configureRefreshControl()
        configureViews()
        
        headlinesPresenter.reloadHeadlineAsync()
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
        headlinesTableView.addSubview(refreshControl)
        headlinesTableView.dataSource = self
        headlinesTableView.delegate = self
        
        headlinesPresenter.view = self
    }
    
    @objc private func handleRefresh() {
        headlinesPresenter.reloadHeadlineAsync()
    }
    
    private func beginRefreshing() {
        headlinesTableView.contentInsetAdjustmentBehavior = .automatic
        refreshControl.beginRefreshing()
    }
    
    private func endRefreshing() {
        headlinesTableView.contentInsetAdjustmentBehavior = .never
        refreshControl.endRefreshing()
    }
}

extension HeadlinesViewController: UITableViewDelegate {
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        if offsetY > 0 {
            let lastVisibleRow = headlinesTableView.indexPathsForVisibleRows?.last?.row
            if lastVisibleRow == articles.count - 1 && !isLoading && !isLastPage {
                headlinesPresenter.loadHeadlineAsync()
            }
        }
    }
}

extension HeadlinesViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return isLastPage || articles.count == 0 ? 1 : 2
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return articles.count
        } else {
            return 1
        }
    }
    
    func tableView(
        _ tableView: UITableView,
        cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        let section = indexPath.section
        if section == 0 {
            let row = indexPath.row
            var cellID: String
            if row == 0 {
                cellID = "Enlarged Headline"
            } else {
                cellID = "Normal Headline"
            }
            let cell = tableView.dequeueReusableCell(
                withIdentifier: cellID) as! HeadlinesTableViewCell
            cell.article = articles[row]
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(
                withIdentifier: "Loading Progress") as! ProgressTableViewCell
            cell.progress.startAnimating()
            return cell
        }
    }
}

extension HeadlinesViewController: HeadlinesView {
    
    func onLoad() {
        NSLog("Loading...")
        isLoading = true
        if headlinesPresenter.isFirstPage {
            isLastPage = false
            beginRefreshing()
        }
    }
    
    func onResponse(response: ArticleListResponse) {
        NSLog("Articles loaded")
        let articles = response.articles ?? []
        if headlinesPresenter.isFirstPage {
            self.articles = articles
            endRefreshing()
        } else {
            self.articles += articles
        }
        headlinesTableView.reloadData()
        isLoading = false
    }
    
    func onLastPageLoaded() {
        NSLog("Last page loaded")
        isLastPage = true
        isLoading = false
    }
    
    func onError(e: KotlinException) {
        if (refreshControl.isRefreshing) {
            endRefreshing()
        } else if (!isLastPage) {
            isLastPage = true
        }
        e.printStackTrace()
        isLoading = false
    }
}
