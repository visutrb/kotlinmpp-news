//
//  HeadlineTableView.swift
//  News
//
//  Created by Visutr Boonnateephisit on 30/3/19.
//  Copyright © 2019 Visutr Boonnateephisit. All rights reserved.
//

import UIKit
import NewsLibrary
import Kingfisher
import DateToolsSwift

class HeadlinesTableViewCell: UITableViewCell {
    
    var article: Article! {
        didSet { updateViews() }
    }
    
    @IBOutlet private weak var headlineImageView: UIImageView!
    @IBOutlet private weak var headlineTitleLabel: UILabel!
    @IBOutlet private weak var headlineSourceLabel: UILabel!
    @IBOutlet private weak var headlineTimeLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(false, animated: animated)
    }
    
    private func updateViews() {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
        
        let publishDate = dateFormatter.date(from: article.publishedAt!)
        
        headlineImageView.kf.setImage(
            with: URL(string: article.urlToImage ?? ""),
            placeholder: UIImage(named: "Placeholder Image"))
        headlineTitleLabel.text = article.title
        headlineSourceLabel.text = article.author ?? article.source?.name ?? "Unknown"
        headlineTimeLabel.text = publishDate?.timeAgoSinceNow
    }
}
