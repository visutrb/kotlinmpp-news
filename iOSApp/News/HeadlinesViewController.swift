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
    
    private var enlargedRowHeight: CGFloat = -1
    private var normalRowHeight: CGFloat = -1
    private var progressRowHeight: CGFloat = -1
    
    private var refreshControl: UIRefreshControl!
    private var isLoading = false
    private var isLastPage = false {
        didSet { headlinesTableView.reloadData() }
    }
    
    private var articles = [Article]()
    private lazy var headlinesPresenter = { PresenterFactory().createHeadlinesPresenter() }()
    
    @IBOutlet private weak var topConstraint: NSLayoutConstraint!
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
}
    
extension HeadlinesViewController: UITableViewDelegate {
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let yOffset = scrollView.contentOffset.y
        if yOffset < 0 {
            topConstraint.constant = -yOffset / 2
        } else {
            topConstraint.constant = 0
        }
    }
    
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let lastVisibleRow = headlinesTableView.indexPathsForVisibleRows?.last?.row
        if lastVisibleRow == articles.count && !isLoading && !isLastPage {
            headlinesPresenter.loadHeadlineAsync()
        }
    }
    
    func tableView(
        _ tableView: UITableView,
        estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat
    {
        let row = indexPath.row
        let rowHeight: CGFloat
        if row == 0 {
            rowHeight = enlargedRowHeight == -1 ? 100 : enlargedRowHeight
        } else if row == articles.count {
            rowHeight = progressRowHeight == -1 ? 0 : progressRowHeight
        } else {
            rowHeight = normalRowHeight == -1 ? 10 : normalRowHeight
        }
        return rowHeight
    }
}

extension HeadlinesViewController: UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return !isLastPage && articles.count != 0 ? articles.count + 1 : articles.count
    }
    
    func tableView(
        _ tableView: UITableView,
        cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        let row = indexPath.row
        if !isLastPage && row == articles.count {
            let cell = tableView.dequeueReusableCell(withIdentifier: "Loading Progress")
                as! ProgressTableViewCell
            cell.progress.startAnimating()
            if progressRowHeight == -1 {
                progressRowHeight = cell.frame.height
            }
            return cell
        } else {
            let cellId = row == 0 ? "Enlarged Headline" : "Normal Headline"
            let cell = tableView.dequeueReusableCell(withIdentifier: cellId)
                as! HeadlinesTableViewCell
            cell.article = articles[row]
            
            if row == 0 && enlargedRowHeight == -1 {
                enlargedRowHeight = cell.frame.height
            } else if row != 0 && normalRowHeight == -1 {
                normalRowHeight = cell.frame.height
            }
            
            return cell
        }
    }
}

extension HeadlinesViewController: HeadlinesView {
    
    func onLoad() {
        NSLog("Loading headlines...")
        isLoading = true
        if headlinesPresenter.isFirstPage {
            isLastPage = false
            refreshControl.beginRefreshing()
        }
    }
    
    func onResponse(response: ArticleListResponse) {
        NSLog("Headlines loaded.")
        let articles = response.articles ?? []
        if headlinesPresenter.isFirstPage {
            self.articles = articles
            refreshControl.endRefreshing()
        } else {
            let insertPosition = articles.count
            var insertIndexPaths = [IndexPath]()
            for index in 0..<articles.count {
                let indexPath = IndexPath(row: insertPosition + index, section: 0)
                insertIndexPaths.append(indexPath)
            }
            self.articles += articles
        }
        headlinesTableView.reloadData()
        isLoading = false
    }
    
    func onLastPageLoaded() {
        NSLog("Loaded last page.")
        isLastPage = true
        isLoading = false
    }
    
    func onError(e: KotlinException) {
        if (refreshControl.isRefreshing) {
            refreshControl.endRefreshing()
        } else if (!isLastPage) {
            isLastPage = true
        }
        e.printStackTrace()
        isLoading = false
    }
}
